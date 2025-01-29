package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen(navController = rememberNavController())
            }
        }
    }
}

class HomeViewModel : ViewModel() {
    private var userName by mutableStateOf("ผู้เยี่ยมชม")
    var showWelcomeMessage by mutableStateOf(true)
    var showHomeMenu by mutableStateOf(false)
    var nickname by mutableStateOf("")

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        nickname = document.getString("nickname") ?: ""
                        userName = nickname.ifBlank { user.email?.substringBefore("@") ?: "ผู้เยี่ยมชม" }
                    } else {
                        userName = user.email?.substringBefore("@") ?: "ผู้เยี่ยมชม"
                    }
                }
                .addOnFailureListener {
                    userName = user.email?.substringBefore("@") ?: "ผู้เยี่ยมชม"
                }
        } else {
            userName = "ผู้เยี่ยมชม"
        }
    }

    fun hideWelcomeMessage() {
        showWelcomeMessage = false
        showHomeMenu = true
    }
}

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Guest"
    val userName = viewModel.nickname.ifBlank { userEmail.substringBefore("@") }
    val scrollState = rememberScrollState()

    // State for Logout Dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Animation States
    var welcomeTextVisible by remember { mutableStateOf(false) }
    var nameTextVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        welcomeTextVisible = true
        delay(500)
        nameTextVisible = true
        delay(2500)
        viewModel.hideWelcomeMessage()
    }

    // Colors
    val primaryColor = Color(0xFF2196F3)
    val primaryVariant = Color(0xFF1976D2)
    val gradientColors = listOf(
        Color(0xFF1E88E5),
        Color(0xFF64B5F6),
        Color(0xFFBBDEFB)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = gradientColors))
    ) {
        if (viewModel.showWelcomeMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1.5f)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = primaryVariant
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val welcomeAlpha by animateFloatAsState(
                                targetValue = if (welcomeTextVisible) 1f else 0f,
                                animationSpec = tween(1000),
                                label = ""
                            )
                            Text(
                                text = "ยินดีต้อนรับ",
                                style = TextStyle(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    letterSpacing = 2.sp
                                ),
                                modifier = Modifier.alpha(welcomeAlpha)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val nameAlpha by animateFloatAsState(
                                targetValue = if (nameTextVisible) 1f else 0f,
                                animationSpec = tween(1000),
                                label = ""
                            )
                            Text(
                                text = userName,
                                style = TextStyle(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryVariant,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.alpha(nameAlpha)
                            )
                        }
                    }
                }
            }
        } else if (viewModel.showHomeMenu) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                MenuButton(
                    text = "BMI",
                    fontSize = 80.sp,
                    onClick = { navController.navigate("bmi_screen") }
                )

                MenuButton(
                    text = "แนะนำโภชนาการ",
                    fontSize = 25.sp,
                    onClick = { navController.navigate("nutrition_screen") }
                )

                MenuButton(
                    text = "โปรแกรมการออกกำลังกาย",
                    fontSize = 25.sp,
                    onClick = { navController.navigate("exercise_screen") }
                )

                MenuButton(
                    text = "จัดการโปรไฟล์",
                    fontSize = 25.sp,
                    onClick = { navController.navigate("profile_screen") }
                )

                MenuButton(
                    text = "ออกจากระบบ",
                    fontSize = 25.sp,
                    onClick = { showLogoutDialog = true }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            FloatingActionButton(
                onClick = { navController.navigate("chatbot_screen") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(64.dp),
                shape = CircleShape,
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Text(
                    text = "Chat",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    // Logout Confirmation Dialog
    LogoutConfirmationDialog(
        showLogoutDialog = showLogoutDialog,
        onDismissRequest = { showLogoutDialog = false },
        onConfirm = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("auth_screen") {
                popUpTo("home_screen") { inclusive = true }
            }
            showLogoutDialog = false
        }
    )
}

@Composable
fun LogoutConfirmationDialog(
    showLogoutDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier
                        .background(
                            color = Color(0xFF2196F3),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ยืนยัน",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE57373),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ยกเลิก",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            },
            title = {
                Text(
                    text = "ออกจากระบบ",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                )
            },
            text = {
                Text(
                    text = "คุณต้องการออกจากระบบไหม?",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                )
            },
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}

fun logout(navController: NavHostController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate("auth_screen") {
        popUpTo("home_screen") { inclusive = true }
    }
}