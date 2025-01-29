package com.example.myapplication2

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // State variables
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var nickname by remember { mutableStateOf(TextFieldValue("")) } // เพิ่ม state สำหรับชื่อเล่น
    var birthDate by remember { mutableStateOf(LocalDate.now()) }
    var gender by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var medicalCondition by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val age = Period.between(birthDate, LocalDate.now()).years

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            birthDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // กำหนด maxDate เป็นวันที่ 31 ธันวาคม 2025
        val maxCalendar = Calendar.getInstance()
        maxCalendar.set(2025, Calendar.DECEMBER, 31)
        this.datePicker.maxDate = maxCalendar.timeInMillis
    }

    // Load profile data from Firebase
    // Load profile data from Firebase
    LaunchedEffect(Unit) {
        loadProfileData(
            onSuccess = { data ->
                name = TextFieldValue(data["name"] as String? ?: "")
                nickname = TextFieldValue(data["nickname"] as String? ?: "") // โหลดชื่อเล่น
                birthDate = LocalDate.parse(data["birthDate"] as String? ?: LocalDate.now().toString())
                gender = data["gender"] as String? ?: ""
                bloodType = data["bloodType"] as String? ?: ""
                height = data["height"] as String? ?: ""
                weight = data["weight"] as String? ?: ""
                medicalCondition = data["medicalCondition"] as String? ?: ""
            },
            onFailure = { error ->
                // Handle error if needed
                Log.e("ProfileScreen", "Error loading profile data: $error")
            }
        )
    }

    // Define your colors
    val primaryColor = Color(0xFF1976D2) // สีน้ำเงินหลัก
    val secondaryColor = Color(0xFF2196F3) // สีน้ำเงินรอง
    val lightBlue = Color(0xFFBBDEFB) // สีน้ำเงินอ่อน
    val darkBlue = Color(0xFF1E88E5) // สีน้ำเงินเข้ม

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ข้อมูลส่วนตัว",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ข้อมูลทั่วไป
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "ข้อมูลทั่วไป",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 20.sp
                        )
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("ชื่อ-นามสกุล *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = primaryColor
                        )
                    )
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("ชื่อเล่น") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = primaryColor
                        )
                    )

                    ElevatedCard(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = lightBlue
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "วันเกิด",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = primaryColor,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                birthDate.format(dateFormatter),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                "อายุ: $age ปี",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = secondaryColor,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // ข้อมูลร่างกาย
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "ข้อมูลร่างกาย",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 20.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (it.length <= 3) height = it },
                            label = { Text("ส่วนสูง (ซม.)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = primaryColor
                            )
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { if (it.length <= 3) weight = it },
                            label = { Text("น้ำหนัก (กก.)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = primaryColor
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("เพศ") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = primaryColor
                            )
                        )

                        OutlinedTextField(
                            value = bloodType,
                            onValueChange = { bloodType = it },
                            label = { Text("กรุ๊ปเลือด") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = primaryColor
                            )
                        )
                    }
                }
            }

            // ข้อมูลสุขภาพ
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "ข้อมูลสุขภาพ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 20.sp
                        )
                    )

                    OutlinedTextField(
                        value = medicalCondition,
                        onValueChange = { medicalCondition = it },
                        label = { Text("โรคประจำตัว") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = primaryColor
                        )
                    )
                }
            }

            // ปุ่มบันทึก
            Button(
                onClick = {
                    if (!validateInput(
                            name = name.text,
                            height = height,
                            weight = weight,
                            gender = gender,
                            birthDate = birthDate,
                            onError = { message ->
                                errorMessage = message
                                showErrorDialog = true
                            }
                        )
                    ) {
                        return@Button
                    }
                    saveProfileData(
                        name.text,
                        nickname.text, // เพิ่มชื่อเล่นเข้าไป
                        birthDate,
                        gender,
                        bloodType,
                        height,
                        weight,
                        medicalCondition,
                        onSuccess = {
                            showSuccessMessage = true
                        },
                        onFailure = { error ->
                            errorMessage = "ไม่สามารถบันทึกข้อมูลได้: $error"
                            showErrorDialog = true
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                )
            ) {
                Text(
                    "บันทึกข้อมูล",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 18.sp
                    )
                )
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("แจ้งเตือน") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("ตกลง", color = primaryColor)
                    }
                },
                containerColor = Color.White,
                titleContentColor = primaryColor,
                textContentColor = Color.Black
            )
        }

        LaunchedEffect(showSuccessMessage) {
            if (showSuccessMessage) {
                snackbarHostState.showSnackbar(
                    message = "บันทึกข้อมูลเรียบร้อยแล้ว",
                    actionLabel = "ตกลง",
                    duration = SnackbarDuration.Short
                )
                showSuccessMessage = false
            }
        }
    }
}

fun validateInput(
    name: String,
    height: String,
    weight: String,
    gender: String,
    birthDate: LocalDate,
    onError: (String) -> Unit
): Boolean {
    return when {
        name.isEmpty() -> {
            onError("กรุณากรอกชื่อ-นามสกุล")
            false
        }
        height.isEmpty() -> {
            onError("กรุณากรอกส่วนสูง")
            false
        }
        weight.isEmpty() -> {
            onError("กรุณากรอกน้ำหนัก")
            false
        }
        gender.isEmpty() -> {
            onError("กรุณากรอกเพศ")
            false
        }
        birthDate.isAfter(LocalDate.now()) -> {
            onError("กรุณาเลือกวันเกิดที่ถูกต้อง")
            false
        }
        else -> true
    }
}

fun loadProfileData(onSuccess: (Map<String, Any>) -> Unit, onFailure: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onFailure("User not logged in")
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                onSuccess(document.data ?: emptyMap())
            } else {
                onFailure("No profile data found")
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception.message ?: "Unknown error")
        }
}
fun saveProfileData(
    name: String,
    nickname: String, // เพิ่ม nickname เข้ามา
    birthDate: LocalDate,
    gender: String,
    bloodType: String,
    height: String,
    weight: String,
    medicalCondition: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onFailure("User not logged in")
    val db = FirebaseFirestore.getInstance()

    val userProfile = mapOf(
        "name" to name,
        "nickname" to nickname, // บันทึกชื่อเล่นลงใน Firestore
        "birthDate" to birthDate.toString(),
        "gender" to gender,
        "bloodType" to bloodType,
        "height" to height,
        "weight" to weight,
        "medicalCondition" to medicalCondition
    )

    db.collection("users").document(userId)
        .set(userProfile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception.message ?: "Unknown error") }
}