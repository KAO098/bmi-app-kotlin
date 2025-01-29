package com.example.myapplication2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BMI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val blueColorScheme = lightColorScheme(
                primary = Color(0xFF1976D2),
                primaryContainer = Color(0xFFBBDEFB),
                secondary = Color(0xFF1565C0),
                secondaryContainer = Color(0xFFE3F2FD),
                surface = Color.White,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onSurface = Color.Black,
                onPrimaryContainer = Color.Black,
                onSecondaryContainer = Color.Black
            )

            MaterialTheme(
                colorScheme = blueColorScheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val historyManager = remember { HistoryManager(this@BMI) }
                    NavHost(navController = navController, startDestination = "bmi_screen") {
                        composable("bmi_screen") {
                            BMIScreen(historyManager, navController)
                        }
                        composable("history_screen") {
                            HistoryScreen(historyManager, navController)
                        }
                    }
                }
            }
        }
    }
}

class HistoryManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("BMI_History", Context.MODE_PRIVATE)
    private val gson = Gson()

    // บันทึกประวัติ
    fun saveHistory(historyItem: HistoryItem) {
        val historyList = getHistory().toMutableList()
        historyList.add(historyItem)
        val json = gson.toJson(historyList)
        sharedPreferences.edit().putString("history", json).apply()
    }

    // ดึงประวัติทั้งหมด
    fun getHistory(): List<HistoryItem> {
        val json = sharedPreferences.getString("history", null)
        return if (json != null) {
            val type = object : TypeToken<List<HistoryItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // ลบประวัติ
    fun deleteHistory(historyItem: HistoryItem) {
        val historyList = getHistory().toMutableList()
        historyList.remove(historyItem) // ลบรายการที่ต้องการ
        val json = gson.toJson(historyList)
        sharedPreferences.edit().putString("history", json).apply()
    }

    // ลบประวัติทั้งหมด
    fun clearHistory() {
        sharedPreferences.edit().remove("history").apply()
    }
}

@Composable
fun BMIScreen(historyManager: HistoryManager, navController: NavController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var activityLevel by remember { mutableStateOf("Sedentary") }
    var bmiResult by remember { mutableStateOf<Float?>(null) }
    var bmiCategory by remember { mutableStateOf("") }
    var dailyCalorie by remember { mutableStateOf<Float?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataFilled by remember { mutableStateOf(false) }

    // Snackbar state for save confirmation
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Calculate BMI when data changes
    LaunchedEffect(weight, height, age, gender, activityLevel) {
        isDataFilled = weight.isNotEmpty() && height.isNotEmpty() && age.isNotEmpty()

        if (isDataFilled) {
            calculateBMI(weight, height, age, onResult = { bmi, category ->
                bmiResult = bmi
                bmiCategory = category
                errorMessage = null
                val bmr = calculateBMR(weight.toFloat(), height.toFloat(), age.toInt(), gender)
                dailyCalorie = calculateDailyCalorie(bmr, activityLevel)
            }, onError = { error ->
                errorMessage = error
                bmiResult = null
                bmiCategory = ""
                dailyCalorie = null
            })
        } else {
            errorMessage = "กรุณากรอกข้อมูลให้ครบทุกช่อง"
            bmiResult = null
            bmiCategory = ""
            dailyCalorie = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card แสดงหัวข้อ
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)) // สีฟ้าแบบ RGB
            ) {
                Text(
                    "คำนวณดัชนีมวลกาย (BMI)\nและแคลอรี่ที่ควรบริโภคต่อวัน",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black // ตัวอักษรสีดำ
                )
            }

            // Input fields
            InputField(
                value = weight,
                onValueChange = { weight = it },
                label = "น้ำหนัก (กิโลกรัม)",
                keyboardType = KeyboardType.Number
            )

            InputField(
                value = height,
                onValueChange = { height = it },
                label = "ส่วนสูง (เซนติเมตร)",
                keyboardType = KeyboardType.Number
            )

            InputField(
                value = age,
                onValueChange = { age = it },
                label = "อายุ (ปี)",
                keyboardType = KeyboardType.Number
            )

            // Reset Button (แสดงเมื่อข้อมูลครบ)
            if (isDataFilled) {
                Button(
                    onClick = {
                        weight = ""
                        height = ""
                        age = ""
                        bmiResult = null
                        bmiCategory = ""
                        dailyCalorie = null
                        errorMessage = null
                        isDataFilled = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "เคลียร์ข้อมูล", color = Color.Black)
                }
            }

            // Gender selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "เพศ",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GenderButton(
                            text = "ชาย",
                            selected = gender == "Male",
                            onClick = { gender = "Male" },
                            modifier = Modifier.weight(1f)
                        )
                        GenderButton(
                            text = "หญิง",
                            selected = gender == "Female",
                            onClick = { gender = "Female" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Activity level selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ระดับกิจกรรม",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ActivityLevelButtons(
                        selectedLevel = activityLevel,
                        onLevelSelected = { activityLevel = it }
                    )
                }
            }

            // Show the error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Display BMI result if available
            bmiResult?.let { bmi ->
                ResultCard(
                    bmi = bmi,
                    category = bmiCategory,
                    weight = weight.toFloatOrNull() ?: 0f,
                    height = height.toFloatOrNull() ?: 0f,
                    age = age.toIntOrNull() ?: 0,
                    gender = gender,
                    activityLevel = activityLevel,
                    dailyCalorie = dailyCalorie ?: 0f
                )

                // ปุ่มบันทึกการคำนวณ
                Button(
                    onClick = {
                        val currentDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                            .format(Date())
                        val historyItem = HistoryItem(
                            id = historyManager.getHistory().size + 1,
                            bmi = bmi,
                            category = bmiCategory,
                            dailyCalorie = dailyCalorie ?: 0f,
                            dateTime = currentDateTime
                        )
                        historyManager.saveHistory(historyItem)

                        // แสดง Snackbar เมื่อบันทึกสำเร็จ
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "บันทึกการคำนวณเรียบร้อยแล้ว",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = "บันทึกการคำนวณ", color = Color.White)
                }
            }

            // ปุ่มดูประวัติ
            Button(
                onClick = {
                    navController.navigate("history_screen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "ดูประวัติ", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
    )
}

@Composable
fun GenderButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = when (text) {
                "ชาย" -> if (selected) Color(0xFF1976D2) else Color(0xFFBBDEFB)
                "หญิง" -> if (selected) Color(0xFFE91E63) else Color(0xFFF8BBD0)
                else -> Color.Gray
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun ActivityLevelButtons(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val levels = listOf(
        "Sedentary" to "กิจกรรมน้อยที่สุด (เหมาะสำหรับคนที่นั่งทำงานเกือบทั้งวัน)",
        "Lightly Active" to "กิจกรรมเบา (เช่น การเดินเล็กน้อยระหว่างวัน)",
        "Moderately Active" to "กิจกรรมปานกลาง (เช่น การออกกำลังกาย 2-3 ครั้งต่อสัปดาห์)",
        "Very Active" to "กิจกรรมมาก (เช่น การออกกำลังกายหนักอย่างสม่ำเสมอ)",
        "Super Active" to "กิจกรรมหนักมาก (เหมาะสำหรับคนที่ออกกำลังกายหนักหรือมีอาชีพที่ต้องใช้แรงงาน)"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        levels.forEach { (level, description) ->
            Button(
                onClick = { onLevelSelected(level) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLevel == level)
                        Color(0xFF1976D2)
                    else
                        Color(0xFFBBDEFB)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = description.split(" (")[0],
                    color = if (selectedLevel == level)
                        Color.White
                    else
                        Color.Black
                )
            }

            if (selectedLevel == level) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ResultCard(
    bmi: Float,
    category: String,
    weight: Float,
    height: Float,
    age: Int,
    gender: String,
    activityLevel: String,
    dailyCalorie: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ผลลัพธ์ BMI: %.1f".format(bmi),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Black
            )
            Text(
                text = "หมวดหมู่: $category",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "แคลอรี่ที่ควรบริโภคต่อวัน: %.0f กิโลแคลอรี่".format(dailyCalorie),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

fun calculateBMI(
    weight: String,
    height: String,
    age: String,
    onResult: (Float, String) -> Unit,
    onError: (String) -> Unit
) {
    if (weight.isEmpty() || height.isEmpty() || age.isEmpty()) {
        onError("กรุณากรอกข้อมูลให้ครบทุกช่อง")
        return
    }

    val weightValue = weight.toFloatOrNull()
    val heightValue = height.toFloatOrNull()
    val ageValue = age.toIntOrNull()

    if (weightValue == null || heightValue == null || ageValue == null) {
        onError("กรุณากรอกข้อมูลในรูปแบบที่ถูกต้อง")
        return
    }

    // เช็คความสมเหตุสมผลของน้ำหนัก
    if (weightValue < 10 || weightValue > 500) {
        onError("น้ำหนักต้องอยู่ในช่วง 10 ถึง 500 กิโลกรัม")
        return
    }

    // เช็คความสมเหตุสมผลของส่วนสูง
    if (heightValue < 50 || heightValue > 250) {
        onError("ส่วนสูงต้องอยู่ในช่วง 50 ถึง 250 เซนติเมตร")
        return
    }

    // เช็คความสมเหตุสมผลของอายุ
    if (ageValue < 1 || ageValue > 120) {
        onError("อายุควรอยู่ในช่วง 1 ถึง 120 ปี")
        return
    }

    val heightInMeters = heightValue / 100
    val bmi = weightValue / (heightInMeters * heightInMeters)
    val category = when {
        bmi < 18.5 -> "น้ำหนักต่ำกว่าเกณฑ์"
        bmi in 18.5..24.9 -> "น้ำหนักปกติ"
        bmi in 25.0..29.9 -> "น้ำหนักเกิน"
        else -> "โรคอ้วน"
    }
    onResult(bmi, category)
}

fun calculateBMR(weight: Float, height: Float, age: Int, gender: String): Float {
    return if (gender == "Male") {
        66.5f + (13.75f * weight) + (5.003f * height) - (6.75f * age)
    } else {
        655.1f + (9.563f * weight) + (1.850f * height) - (4.676f * age)
    }
}

fun calculateDailyCalorie(bmr: Float, activityLevel: String): Float {
    val activityFactor = when (activityLevel) {
        "Sedentary" -> 1.2f
        "Lightly Active" -> 1.375f
        "Moderately Active" -> 1.55f
        "Very Active" -> 1.725f
        "Super Active" -> 1.9f
        else -> 1.2f
    }
    return bmr * activityFactor
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(historyManager: HistoryManager, navController: NavController) {
    var historyList by remember { mutableStateOf(historyManager.getHistory()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredHistory = if (searchQuery.isEmpty()) {
        historyList
    } else {
        historyList.filter { it.dateTime.startsWith(searchQuery) }
    }

    val firstBMI = historyList.firstOrNull()?.bmi ?: 0f
    val latestBMI = historyList.lastOrNull()?.bmi ?: 0f
    val bmiCategory = when {
        latestBMI < 18.5 -> "Underweight"
        latestBMI in 18.5..24.9 -> "Normal"
        latestBMI in 25.0..29.9 -> "Overweight"
        else -> "Obese"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ประวัติการคำนวณ",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "กลับ",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFBBDEFB)
                        )
                    )
                )
        ) {
            // Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("ค้นหาวันที่ (dd/MM/yyyy)") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "ค้นหา") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            // Summary Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "สรุปผลลัพธ์",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFA5D6A7))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "BMI แรก",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "%.1f".format(firstBMI),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF81D4FA))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "BMI ล่าสุด",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "%.1f".format(latestBMI),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recommendation Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "คำแนะนำสุขภาพ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            getBMIRecommendation(firstBMI, latestBMI),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // History List Section
            item {
                Text(
                    "บันทึกการคำนวณ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ไม่มีประวัติการคำนวณ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(filteredHistory) { item ->
                    HistoryItemCard(
                        historyItem = item,
                        onDelete = {
                            historyManager.deleteHistory(item)
                            historyList = historyManager.getHistory()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    historyItem: HistoryItem,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete()
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF5F5F5),
                            Color(0xFFE0E0E0)
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = historyItem.dateTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BMI: ${String.format("%.1f", historyItem.bmi)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                historyItem.bmi < 18.5 -> Color(0xFF2196F3)
                                historyItem.bmi in 18.5..24.9 -> Color(0xFF4CAF50)
                                historyItem.bmi in 25.0..29.9 -> Color(0xFFFFC107)
                                else -> Color(0xFFF44336)
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${historyItem.category})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "แคลอรี่: ${String.format("%.0f", historyItem.dailyCalorie)} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "ลบ",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "ยืนยันการลบ", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Text(text = "คุณแน่ใจหรือไม่ว่าต้องการลบรายการนี้?", style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "ตกลง", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "ยกเลิก", color = Color.Black)
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = Color.Black
    )
}
fun getBMIRecommendation(firstBMI: Float, latestBMI: Float): String {
    val bmiDifference = latestBMI - firstBMI
    val bmiCategory = when {
        latestBMI < 18.5 -> "Underweight"
        latestBMI in 18.5..24.9 -> "Normal"
        latestBMI in 25.0..29.9 -> "Overweight"
        else -> "Obese"
    }

    val recommendation = when {
        // กรณี BMI ลดลง
        bmiDifference < 0 && latestBMI < 18.5 -> {
            "คุณมี BMI ลดลง %.1f และอยู่ในเกณฑ์น้ำหนักต่ำกว่าเกณฑ์\n".format(-bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- เพิ่มแคลอรีประมาณ 300–500 กิโลแคลอรีต่อวัน\n" +
                    "- เน้นอาหารโปรตีนสูง เช่น อกไก่ ปลา ไข่ ถั่ว\n" +
                    "- เพิ่มคาร์โบไฮเดรตเชิงซ้อน เช่น ข้าวกล้อง ข้าวโอ๊ต\n" +
                    "- ออกกำลังกายสร้างกล้ามเนื้อ เช่น สควอท ดันพื้น\n" +
                    "- หลีกเลี่ยงคาร์ดิโอหนัก และนอนหลับให้เพียงพอ"
        }
        bmiDifference < 0 && latestBMI in 18.5..24.9 -> {
            "คุณมี BMI ลดลง %.1f และอยู่ในเกณฑ์น้ำหนักปกติ\n".format(-bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- รักษาสมดุลการกินและการออกกำลังกาย\n" +
                    "- ตรวจสอบแคลอรีที่บริโภคให้สอดคล้องกับ TDEE"
        }
        bmiDifference < 0 && latestBMI in 25.0..29.9 -> {
            "คุณมี BMI ลดลง %.1f และอยู่ในเกณฑ์น้ำหนักเกิน\n".format(-bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- ลดแคลอรีประมาณ 250–500 กิโลแคลอรีต่อวัน\n" +
                    "- เพิ่มการออกกำลังกายคาร์ดิโอ เช่น วิ่ง ปั่นจักรยาน\n" +
                    "- เน้นผักและผลไม้ไม่หวาน"
        }
        bmiDifference < 0 && latestBMI >= 30 -> {
            "คุณมี BMI ลดลง %.1f และอยู่ในเกณฑ์โรคอ้วน\n".format(-bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- ลดแคลอรีประมาณ 750–1,000 กิโลแคลอรีต่อวัน\n" +
                    "- ปรึกษาแพทย์หรือนักโภชนาการ\n" +
                    "- ออกกำลังกายเบาๆ เช่น เดิน โยคะ"
        }

        // กรณี BMI เพิ่มขึ้น
        bmiDifference > 0 && latestBMI < 18.5 -> {
            "คุณมี BMI เพิ่มขึ้น %.1f และอยู่ในเกณฑ์น้ำหนักต่ำกว่าเกณฑ์\n".format(bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- เพิ่มแคลอรีประมาณ 300–500 กิโลแคลอรีต่อวัน\n" +
                    "- เน้นอาหารโปรตีนสูง เช่น อกไก่ ปลา ไข่ ถั่ว\n" +
                    "- เพิ่มคาร์โบไฮเดรตเชิงซ้อน เช่น ข้าวกล้อง ข้าวโอ๊ต\n" +
                    "- ออกกำลังกายสร้างกล้ามเนื้อ เช่น สควอท ดันพื้น\n" +
                    "- หลีกเลี่ยงคาร์ดิโอหนัก และนอนหลับให้เพียงพอ"
        }
        bmiDifference > 0 && latestBMI in 18.5..24.9 -> {
            "คุณมี BMI เพิ่มขึ้น %.1f และอยู่ในเกณฑ์น้ำหนักปกติ\n".format(bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- รักษาสมดุลการกินและการออกกำลังกาย\n" +
                    "- ตรวจสอบแคลอรีที่บริโภคให้สอดคล้องกับ TDEE"
        }
        bmiDifference > 0 && latestBMI in 25.0..29.9 -> {
            "คุณมี BMI เพิ่มขึ้น %.1f และอยู่ในเกณฑ์น้ำหนักเกิน\n".format(bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- ลดแคลอรีประมาณ 250–500 กิโลแคลอรีต่อวัน\n" +
                    "- เพิ่มการออกกำลังกายคาร์ดิโอ เช่น วิ่ง ปั่นจักรยาน\n" +
                    "- เน้นผักและผลไม้ไม่หวาน"
        }
        bmiDifference > 0 && latestBMI >= 30 -> {
            "คุณมี BMI เพิ่มขึ้น %.1f และอยู่ในเกณฑ์โรคอ้วน\n".format(bmiDifference) +
                    "คำแนะนำ:\n" +
                    "- ลดแคลอรีประมาณ 750–1,000 กิโลแคลอรีต่อวัน\n" +
                    "- ปรึกษาแพทย์หรือนักโภชนาการ\n" +
                    "- ออกกำลังกายเบาๆ เช่น เดิน โยคะ"
        }

        // กรณีอื่นๆ (BMI ไม่เปลี่ยนแปลง)
        else -> {
            "BMI ของคุณไม่เปลี่ยนแปลง\n" +
                    "คำแนะนำ:\n" +
                    "- รักษาสมดุลการกินและการออกกำลังกาย\n" +
                    "- ตรวจสอบแคลอรีที่บริโภคให้สอดคล้องกับ TDEE"
        }
    }

    // เพิ่มโปรแกรมออกกำลังกาย
    val exerciseProgram = getExerciseProgram(bmiCategory)

    // รวมคำแนะนำสุขภาพและโปรแกรมออกกำลังกาย
    return "$recommendation\n\n$exerciseProgram"
}
fun getExerciseProgram(bmiCategory: String): String {
    return when (bmiCategory) {
        "Underweight" -> {
            "โปรแกรมออกกำลังกายสำหรับน้ำหนักต่ำกว่าเกณฑ์:\n" +
                    "- สควอท: 3 เซ็ต เซ็ตละ 10–12 ครั้ง\n" +
                    "- ดันพื้น: 3 เซ็ต เซ็ตละ 8–10 ครั้ง\n" +
                    "- เดดลิฟท์: 3 เซ็ต เซ็ตละ 10–12 ครั้ง\n" +
                    "- ท่าแพลงก์: ค้างไว้ 30–60 วินาที\n" +
                    "- โยคะ: ท่าสุริยนมัสการ 5 รอบ"
        }
        "Normal" -> {
            "โปรแกรมออกกำลังกายสำหรับน้ำหนักปกติ:\n" +
                    "- วิ่งเหยาะๆ: 30 นาที\n" +
                    "- เต้นซุมบ้า: 30 นาที\n" +
                    "- ปั่นจักรยาน: 45 นาที\n" +
                    "- ยกน้ำหนัก: 30 นาที\n" +
                    "- โยคะ: ท่าสุริยนมัสการ 5 รอบ"
        }
        "Overweight" -> {
            "โปรแกรมออกกำลังกายสำหรับน้ำหนักเกิน:\n" +
                    "- เดินเร็ว: 45 นาที\n" +
                    "- ปั่นจักรยานอยู่กับที่: 30 นาที\n" +
                    "- แอโรบิก: 30 นาที\n" +
                    "- สควอท: 3 เซ็ต เซ็ตละ 10–12 ครั้ง\n" +
                    "- โยคะ: ท่าสุริยนมัสการ 5 รอบ"
        }
        "Obese" -> {
            "โปรแกรมออกกำลังกายสำหรับโรคอ้วน:\n" +
                    "- เดิน: 30 นาที\n" +
                    "- การยืดเหยียด: 15 นาที\n" +
                    "- โยคะเบื้องต้น: 20 นาที\n" +
                    "- ออกกำลังกายด้วยเก้าอี้: 20 นาที\n" +
                    "- ไทชิ: 20 นาที"
        }
        else -> "โปรแกรมออกกำลังกายไม่รู้"
    }
}