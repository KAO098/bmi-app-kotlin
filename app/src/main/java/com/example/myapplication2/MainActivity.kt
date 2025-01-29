package com.example.myapplication2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationGraph(navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun NavigationGraph(navController: NavHostController) {
    val context = LocalContext.current
    val historyManager = remember { HistoryManager(context) } // สร้าง HistoryManager

    NavHost(
        navController = navController,
        startDestination = "auth_screen"
    ) {
        composable("auth_screen") {
            AuthScreen(navController = navController)
        }
        composable("home_screen") {
            HomeScreen(navController = navController)
        }
        composable("bmi_screen") {
            BMIScreen(historyManager = historyManager, navController = navController)
        }
        composable("history_screen") { // เพิ่ม route สำหรับ history_screen
            HistoryScreen(historyManager = historyManager, navController = navController)
        }
        composable("nutrition_screen") {
            NutritionScreen()
        }
        composable("exercise_screen") {
            ExerciseScreen()
        }
        composable("profile_screen") {
            ProfileScreen()
        }
        composable("chatbot_screen") {
            val viewModel: ChatViewModel = viewModel()
            ChatbotScreen(viewModel = viewModel)
        }
    }
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun getCurrentUser() = firebaseAuth.currentUser
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var isForgotPassword by remember { mutableStateOf(false) }

    val onAuthSuccess = {
        if (isRegistering) {
            // หลังจากการสมัครสำเร็จให้กลับไปที่หน้า Login
            navController.navigate("auth_screen") {
                popUpTo("auth_screen") { inclusive = true }
            }
        } else {
            // ถ้าเป็นการล็อกอิน ให้ไปที่หน้า Home
            navController.navigate("home_screen") {
                popUpTo("auth_screen") { inclusive = true }
            }
        }
    }


    val onAuthError: (String) -> Unit = { message ->
        errorMessage = message
        email = ""
        password = ""
        confirmPassword = ""
    }

    if (isForgotPassword) {
        ForgotPasswordScreen(
            onSuccess = {
                isForgotPassword = false
                errorMessage = "กรุณาตรวจสอบอีเมลของคุณเพื่อรีเซ็ตรหัสผ่าน"
            },
            onError = { errorMessage = it },
            onBackToLogin = { isForgotPassword = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .border(2.dp, Color(0xFF6200EE), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bmilogo),
                    contentDescription = "BMI Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = if (isRegistering) "Create Account" else "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6200EE)
            )

            Text(
                text = if (isRegistering) "Sign up to get started" else "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Fields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                isError = errorMessage.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6200EE)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF6200EE)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6200EE)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = Color(0xFF6200EE)
                    )
                }
            )

            if (isRegistering) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = errorMessage.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF6200EE),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF6200EE)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password",
                            tint = Color(0xFF6200EE)
                        )
                    }
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "กรุณากรอกอีเมลและรหัสผ่าน"
                    } else if (isRegistering && password != confirmPassword) {
                        errorMessage = "รหัสผ่านไม่ตรงกัน"
                    } else {
                        errorMessage = ""
                        if (isRegistering) {
                            authViewModel.registerWithEmailAndPassword(
                                email,
                                password,
                                onSuccess = onAuthSuccess,
                                onError = onAuthError
                            )
                        } else {
                            authViewModel.loginWithEmailAndPassword(
                                email,
                                password,
                                onSuccess = onAuthSuccess,
                                onError = onAuthError
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = if (isRegistering) "Create Account" else "Sign In",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle and Forgot Password
            if (!isRegistering) {
                TextButton(
                    onClick = { isForgotPassword = true }
                ) {
                    Text(text = "Forgot Password?", color = Color(0xFF6200EE))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRegistering) "Already have an account? " else "Don't have an account? ",
                    color = Color.Gray
                )
                TextButton(
                    onClick = {
                        email = ""
                        password = ""
                        confirmPassword = ""
                        errorMessage = ""
                        isRegistering = !isRegistering
                    }
                ) {
                    Text(
                        text = if (isRegistering) "Sign In" else "Sign Up",
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon และ Header
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Reset Password",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = Color(0xFF6200EE)
        )

        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF6200EE),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // คำอธิบาย
        Text(
            text = "กรุณากรอกอีเมลของคุณ\nเราจะส่งลิงก์สำหรับรีเซ็ตรหัสผ่านไปให้",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,  // เปลี่ยนเป็นสีดำเข้ม
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            isError = errorMessage.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6200EE),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF6200EE)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color(0xFF6200EE)
                )
            },
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Send Reset Link Button
        Button(
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "กรุณากรอกอีเมล"
                } else {
                    isLoading = true
                    errorMessage = ""
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(task.exception?.message ?: "ไม่สามารถส่งอีเมลรีเซ็ตรหัสผ่านได้")
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "ส่งลิงก์รีเซ็ตรหัสผ่าน",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Back to Login Button
        TextButton(
            onClick = { onBackToLogin() },
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Login",
                    tint = Color(0xFF6200EE),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "กลับไปหน้าเข้าสู่ระบบ",
                    color = Color(0xFF6200EE)
                )
            }
        }
    }
}