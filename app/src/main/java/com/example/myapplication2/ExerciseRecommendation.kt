package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Activity หลัก
class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // เปิดใช้งาน edge-to-edge
        enableEdgeToEdge()
        // กำหนดเนื้อหาของ Activity
        setContent {
            // ใช้ธีมที่กำหนดเอง
            AppTheme {
                // แสดงหน้าจอหลัก
                ExerciseScreen()
            }
        }
    }
}

// ธีมของแอปพลิเคชัน
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // กำหนดโทนสี
    val colorScheme = lightColorScheme(
        primary = Color(0xFF006C51), // สีหลัก
        onPrimary = Color.White, // สีบนสีหลัก
        primaryContainer = Color(0xFF8FF8D3), // สีคอนเทนเนอร์หลัก
        onPrimaryContainer = Color(0xFF002117), // สีบนคอนเทนเนอร์หลัก
        secondary = Color(0xFF4C6358), // สีรอง
        onSecondary = Color.White, // สีบนสีรอง
        secondaryContainer = Color(0xFFCEE9DA), // สีคอนเทนเนอร์รอง
        onSecondaryContainer = Color(0xFF092016), // สีบนคอนเทนเนอร์รอง
        tertiary = Color(0xFF3D6374), // สีตติยภูมิ
        onTertiary = Color.White, // สีบนสีตติยภูมิ
        tertiaryContainer = Color(0xFFC1E8FC), // สีคอนเทนเนอร์ตติยภูมิ
        onTertiaryContainer = Color(0xFF001F2A), // สีบนคอนเทนเนอร์ตติยภูมิ
        error = Color(0xFFBA1A1A), // สีข้อผิดพลาด
        errorContainer = Color(0xFFFFDAD6), // สีคอนเทนเนอร์ข้อผิดพลาด
        onError = Color.White, // สีบนสีข้อผิดพลาด
        onErrorContainer = Color(0xFF410002), // สีบนคอนเทนเนอร์ข้อผิดพลาด
        background = Color(0xFFFBFDF9), // สีพื้นหลัง
        onBackground = Color(0xFF191C1A), // สีบนพื้นหลัง
        surface = Color(0xFFFBFDF9), // สีพื้นผิว
        onSurface = Color(0xFF191C1A), // สีบนพื้นผิว
        surfaceVariant = Color(0xFFDBE5DD), // สีพื้นผิวที่แตกต่าง
        onSurfaceVariant = Color(0xFF404943), // สีบนพื้นผิวที่แตกต่าง
        outline = Color(0xFF707973) // สีเส้นขอบ
    )

    // กำหนดรูปแบบตัวอักษร
    val typography = Typography(
        headlineLarge = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold
        ),
        titleLarge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
            fontSize = 15.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        labelLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        )
    )

    // ใช้ MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp)
        ),
        content = content
    )
}

// พรีวิวหน้าจอหลัก
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        ExerciseScreen()
    }
}

// ข้อมูลผู้ใช้
data class UserProfile(
    val age: Int,
    val gender: Gender,
    val fitnessLevel: FitnessLevel,
    val bmiRange: BmiRange
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class FitnessLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class BmiRange {
    UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE
}

data class Exercise(
    val name: String,
    val duration: String,
    val description: String
)

data class ExerciseProgram(
    val name: String,
    val exercises: List<Exercise>
)

// คลาสแนะนำโปรแกรมออกกำลังกาย
object ExerciseRecommendations {
    // โปรแกรมสำหรับผู้มีน้ำหนักต่ำกว่าเกณฑ์
    val UnderweightPrograms = listOf(
        ExerciseProgram("โปรแกรมสร้างกล้ามเนื้อ", listOf(
            Exercise("สควอท", "3 เซ็ต เซ็ตละ 12 ครั้ง", "ยืนแยกขาเท่าความกว้างไหล่ ย่อตัวลงจนต้นขาขนานกับพื้น แล้วดันตัวขึ้น"),
            Exercise("ดันพื้น", "3 เซ็ต เซ็ตละ 10 ครั้ง", "วางมือบนพื้น กว้างเท่าช่วงไหล่ ลดตัวลงจนหน้าอกแตะพื้น แล้วดันตัวขึ้น"),
            Exercise("เดดลิฟท์", "3 เซ็ต เซ็ตละ 12 ครั้ง", "ยืนหน้าบาร์เบล ย่อตัวลงจับบาร์เบล ยกบาร์เบลขึ้นโดยหลังตรง")
        )),
        ExerciseProgram("โยคะเพื่อสุขภาพ", listOf(
            Exercise("ท่าสุริยนมัสการ", "5 รอบ", "ชุดท่าโยคะที่ประกอบด้วยท่าต่างๆ เชื่อมต่อกัน"),
            Exercise("ท่ายืนบิดตัว", "ค้างไว้ 30 วินาที", "ยืนตรง บิดตัวไปด้านข้าง"),
            Exercise("ท่าต้นไม้", "ค้างไว้ 30 วินาที", "ยืนขาเดียว มือพนมเหนือศีรษะ")
        ))
    )

    // โปรแกรมสำหรับผู้มีน้ำหนักปกติ
    val NormalPrograms = listOf(
        ExerciseProgram("โปรแกรมคาร์ดิโอ", listOf(
            Exercise("วิ่งเหยาะๆ", "30 นาที", "วิ่งด้วยความเร็วปานกลาง"),
            Exercise("เต้นซุมบ้า", "30 นาที", "เต้นตามจังหวะเพลงละติน"),
            Exercise("ปั่นจักรยาน", "45 นาที", "ปั่นด้วยความเร็วปานกลาง สลับกับความเร็วสูง")
        )),
        ExerciseProgram("โปรแกรมฟิตเนส", listOf(
            Exercise("เดินบนลู่วิ่ง", "20 นาที", "เดินบนลู่วิ่ง ปรับความชันและความเร็วได้"),
            Exercise("ยกน้ำหนัก", "30 นาที", "เลือกท่าบริหารกล้ามเนื้อส่วนต่างๆ"),
            Exercise("เล่นเครื่องออกกำลังกาย", "20 นาที", "เลือกเครื่องบริหารกล้ามเนื้อส่วนต่างๆ")
        ))
    )

    // โปรแกรมสำหรับผู้มีน้ำหนักเกิน
    val OverweightPrograms = listOf(
        ExerciseProgram("โปรแกรมลดน้ำหนัก", listOf(
            Exercise("เดินเร็ว", "45 นาที", "เดินด้วยความเร็วที่ทำให้รู้สึกเหนื่อย"),
            Exercise("ปั่นจักรยานอยู่กับที่", "30 นาที", "ปั่นจักรยานด้วยความเร็วปานกลาง ปรับแรงต้านได้"),
            Exercise("แอโรบิก", "30 นาที", "เต้นแอโรบิกตามคลิปวิดีโอ")
        )),
        ExerciseProgram("โปรแกรมคาร์ดิโอเบาๆ", listOf(
            Exercise("ปั่นจักรยานช้าๆ", "30 นาที", "ปั่นจักรยานด้วยความเร็วช้า"),
            Exercise("เดินบนลู่วิ่ง", "30 นาที", "เดินบนลู่วิ่ง ปรับความชันเล็กน้อย"),
            Exercise("โยคะ", "30 นาที", "ฝึกโยคะท่าพื้นฐาน")
        ))
    )

    // โปรแกรมสำหรับผู้เป็นโรคอ้วน
    val ObesePrograms = listOf(
        ExerciseProgram("โปรแกรมเริ่มต้น", listOf(
            Exercise("เดิน", "30 นาที", "เดินช้าๆ สะสม 30 นาทีต่อวัน"),
            Exercise("การยืดเหยียด", "15 นาที", "ยืดกล้ามเนื้อแขน ขา และหลัง"),
            Exercise("โยคะเบื้องต้น", "20 นาที", "ฝึกท่าหายใจและท่าโยคะง่ายๆ")
        )),
        ExerciseProgram("โปรแกรมเคลื่อนไหวร่างกาย", listOf(
            Exercise("ออกกำลังกายด้วยเก้าอี้", "20 นาที", "ยืดเหยียดและบริหารกล้ามเนื้อบนเก้าอี้"),
            Exercise("ไทชิ", "20 นาที", "ฝึกการเคลื่อนไหวช้าๆ ควบคู่กับการหายใจ"),
            Exercise("กายภาพบำบัด", "30 นาที", "ฝึกกายภาพบำบัดตามคำแนะนำของนักกายภาพ")
        ))
    )

    // ฟังก์ชันแนะนำโปรแกรมตามโปรไฟล์ผู้ใช้
    fun getPersonalizedRecommendations(userProfile: UserProfile): List<ExerciseProgram> {
        // เลือกโปรแกรมพื้นฐานตามช่วง BMI
        val basePrograms = when (userProfile.bmiRange) {
            BmiRange.UNDERWEIGHT -> UnderweightPrograms
            BmiRange.NORMAL -> NormalPrograms
            BmiRange.OVERWEIGHT -> OverweightPrograms
            BmiRange.OBESE -> ObesePrograms
        }

        // ปรับแต่งโปรแกรมตามระดับความฟิต
        return when (userProfile.fitnessLevel) {
            FitnessLevel.BEGINNER -> {
                // สำหรับผู้เริ่มต้น: ลดระยะเวลา และตัดท่าที่ยาก
                basePrograms.map { program ->
                    program.copy(exercises = program.exercises.map { exercise ->
                        exercise.copy(duration = adjustDurationForBeginner(exercise.duration, userProfile.age))
                    })
                }.filter { program ->
                    !listOf("เดดลิฟท์", "ยกน้ำหนัก").any { exerciseName ->
                        program.exercises.any { it.name.contains(exerciseName) }
                    }
                }
            }
            FitnessLevel.INTERMEDIATE -> {
                // สำหรับระดับปานกลาง: ปรับระยะเวลาเดินเร็ว
                basePrograms.map { program ->
                    program.copy(exercises = program.exercises.map { exercise ->
                        if (exercise.name == "เดินเร็ว") {
                            exercise.copy(duration = "45-60 นาที")
                        } else {
                            exercise
                        }
                    })
                }
            }
            FitnessLevel.ADVANCED -> {
                // สำหรับระดับสูง: เพิ่มท่ายกน้ำหนักสำหรับผู้ชายในโปรแกรมสร้างกล้ามเนื้อ
                basePrograms.map { program ->
                    if (userProfile.gender == Gender.MALE && program.name == "โปรแกรมสร้างกล้ามเนื้อ") {
                        program.copy(exercises = program.exercises.toMutableList().apply {
                            add(Exercise("ยกน้ำหนัก", "3 เซ็ต เซ็ตละ 10-12 ครั้ง", "เน้นท่าที่บริหารกล้ามเนื้อมัดใหญ่"))
                        })
                    } else {
                        program
                    }
                }
            }
        }
    }

    // ฟังก์ชันปรับระยะเวลาสำหรับผู้เริ่มต้น
    private fun adjustDurationForBeginner(duration: String, age: Int): String {
        return when {
            duration.contains("เซ็ต") -> {
                // ลดจำนวนเซ็ตสำหรับผู้เริ่มต้น
                val sets = duration.substringBefore(" ").toIntOrNull() ?: return duration
                val reducedSets = (sets * 0.7).toInt() // ลดลง 30%
                duration.replace("$sets เซ็ต", "$reducedSets เซ็ต")
            }
            duration.contains("นาที") -> {
                // ลดระยะเวลาสำหรับผู้เริ่มต้น
                val minutes = duration.substringBefore(" ").toIntOrNull() ?: return duration
                val reducedMinutes = if (age >= 65) {
                    (minutes * 0.5).toInt() // ผู้สูงอายุ ลดลง 50%
                } else {
                    (minutes * 0.7).toInt() // ลดลง 30%
                }
                duration.replace("$minutes นาที", "$reducedMinutes นาที")
            }
            duration.contains("รอบ") -> {
                // ลดจำนวนรอบสำหรับผู้เริ่มต้น
                val rounds = duration.substringBefore(" ").toIntOrNull() ?: return duration
                val reducedRounds = (rounds * 0.6).toInt() // ลดลง 40%
                duration.replace("$rounds รอบ", "$reducedRounds รอบ")
            }
            else -> duration
        }
    }
}

// หน้าจอหลักของแอป
@Composable
fun ExerciseScreen() {
    // สถานะของช่วง BMI ที่เลือก
    var selectedBmiRange by remember { mutableStateOf<BmiRange?>(null) }
    // สถานะของโปรไฟล์ผู้ใช้
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    // สถานะแสดงหน้าจอ
    var showInputScreen by remember { mutableStateOf(true) }

    // แสดงหน้าจอตามสถานะ
    if (showInputScreen) {
        // แสดงหน้ากรอกข้อมูล
        InputScreen(onProfileSubmitted = { profile ->
            userProfile = profile
            showInputScreen = false
        })
    } else {
        // แสดงหน้าจอ Scaffold
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                // สลับการแสดงผลระหว่างหน้าเลือก BMI และหน้ารายการโปรแกรม
                Crossfade(targetState = userProfile, label = "") { profile ->
                    if (profile != null) {
                        // แสดงรายการโปรแกรมที่แนะนำ
                        val recommendedPrograms = ExerciseRecommendations.getPersonalizedRecommendations(profile)
                        ExerciseProgramList(
                            programs = recommendedPrograms,
                            bmiRangeText = profile.bmiRange.name,
                            onBackPressed = { showInputScreen = true }
                        )
                    } else {
                        // แสดงหน้าเลือกช่วง BMI
                        BmiRangeSelection(
                            onRangeSelected = { range ->
                                selectedBmiRange = range
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InputScreen(onProfileSubmitted: (UserProfile) -> Unit) {
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<Gender?>(null) }
    var fitnessLevel by remember { mutableStateOf<FitnessLevel?>(null) }
    var bmiRange by remember { mutableStateOf<BmiRange?>(null) }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Decorative header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = "กรอกข้อมูลส่วนตัว",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Age Input with Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "อายุ",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("อายุ (ปี)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Selection with Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    GenderSelection(
                        selectedGender = gender,
                        onGenderSelected = { gender = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fitness Level Selection with Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FitnessLevelSelection(
                        selectedLevel = fitnessLevel,
                        onLevelSelected = { fitnessLevel = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BMI Range Dropdown with Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BmiRangeDropdown(
                        selectedBmiRange = bmiRange,
                        onBmiRangeSelected = { bmiRange = it }
                    )
                }
            }

            // Error Message
            AnimatedVisibility(visible = showError) {
                Text(
                    text = "กรุณากรอกข้อมูลให้ครบ",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button with Gradient
            Button(
                onClick = {
                    if (age.isNotEmpty() && gender != null && fitnessLevel != null && bmiRange != null) {
                        onProfileSubmitted(
                            UserProfile(
                                age.toInt(),
                                gender!!,
                                fitnessLevel!!,
                                bmiRange!!
                            )
                        )
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "ถัดไป",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// คอมโพเนนต์สำหรับเลือกเพศ
@Composable
fun GenderSelection(selectedGender: Gender?, onGenderSelected: (Gender) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "เพศ",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Gender.entries.forEach { gender ->
                OutlinedButton(
                    onClick = { onGenderSelected(gender) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedGender == gender) {
                        ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        ButtonDefaults.outlinedButtonBorder
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = when (gender) {
                            Gender.MALE -> "ชาย"
                            Gender.FEMALE -> "หญิง"
                            Gender.OTHER -> "อื่นๆ"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

// คอมโพเนนต์สำหรับเลือกระดับความฟิต
@Composable
fun FitnessLevelSelection(selectedLevel: FitnessLevel?, onLevelSelected: (FitnessLevel) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ระดับความฟิต",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column {
            FitnessLevel.values().forEach { level ->
                OutlinedButton(
                    onClick = { onLevelSelected(level) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = if (selectedLevel == level) {
                        ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        ButtonDefaults.outlinedButtonBorder
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = when (level) {
                            FitnessLevel.BEGINNER -> "มือใหม่"
                            FitnessLevel.INTERMEDIATE -> "ระดับกลาง"
                            FitnessLevel.ADVANCED -> "ระดับสูง"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


// คอมโพเนนต์สำหรับเลือกช่วง BMI (Dropdown)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiRangeDropdown(selectedBmiRange: BmiRange?, onBmiRangeSelected: (BmiRange) -> Unit) {
    // สถานะการเปิด/ปิดเมนู
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // หัวข้อ "ช่วง BMI"
        Text(
            text = "ช่วง BMI",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // คอมโพเนนต์ ExposedDropdownMenuBox
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // ช่องแสดงช่วง BMI ที่เลือก
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = when (selectedBmiRange) {
                    BmiRange.UNDERWEIGHT -> "น้ำหนักต่ำกว่าเกณฑ์ (BMI < 18.5)"
                    BmiRange.NORMAL -> "น้ำหนักปกติ (BMI 18.5-24.9)"
                    BmiRange.OVERWEIGHT -> "น้ำหนักเกิน (BMI 25-29.9)"
                    BmiRange.OBESE -> "โรคอ้วน (BMI ≥ 30)"
                    else -> ""
                },
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(16.dp) // เพิ่ม shape ให้กับ OutlinedTextField
            )
            // เมนูตัวเลือก
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface) // กำหนดสีพื้นหลังเมนู
            ) {
                // แสดงตัวเลือกช่วง BMI
                DropdownMenuItem(
                    text = { Text("น้ำหนักต่ำกว่าเกณฑ์ (BMI < 18.5)", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onBmiRangeSelected(BmiRange.UNDERWEIGHT)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenuItem(
                    text = { Text("น้ำหนักปกติ (BMI 18.5-24.9)", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onBmiRangeSelected(BmiRange.NORMAL)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenuItem(
                    text = { Text("น้ำหนักเกิน (BMI 25-29.9)", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onBmiRangeSelected(BmiRange.OVERWEIGHT)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenuItem(
                    text = { Text("โรคอ้วน (BMI ≥ 30)", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onBmiRangeSelected(BmiRange.OBESE)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/// คอมโพเนนต์สำหรับเลือกช่วง BMI (ปุ่ม)
@Composable
fun BmiRangeSelection(onRangeSelected: (BmiRange) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // หัวข้อ "เลือกช่วง BMI ของคุณ"
        Text(
            text = "เลือกช่วง BMI ของคุณ",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // รายการช่วง BMI และป้ายกำกับ
        val bmiRanges = listOf(
            "น้ำหนักต่ำกว่าเกณฑ์ (BMI < 18.5)" to BmiRange.UNDERWEIGHT,
            "น้ำหนักปกติ (BMI 18.5-24.9)" to BmiRange.NORMAL,
            "น้ำหนักเกิน (BMI 25-29.9)" to BmiRange.OVERWEIGHT,
            "โรคอ้วน (BMI ≥ 30)" to BmiRange.OBESE
        )

        // แสดงปุ่มสำหรับแต่ละช่วง BMI
        bmiRanges.forEach { (label, range) ->
            ElevatedButton(
                onClick = { onRangeSelected(range) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// คอมโพเนนต์สำหรับแสดงรายการโปรแกรมออกกำลังกาย
@Composable
fun ExerciseProgramList(
    programs: List<ExerciseProgram>,
    bmiRangeText: String,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // หัวข้อ "แนะนำโปรแกรมออกกำลังกาย"
        Text(
            text = "แนะนำโปรแกรมออกกำลังกาย",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // แสดงช่วง BMI ที่เลือก
        Text(
            text = when (bmiRangeText) {
                "UNDERWEIGHT" -> "น้ำหนักต่ำกว่าเกณฑ์ (BMI < 18.5)"
                "NORMAL" -> "น้ำหนักปกติ (BMI 18.5-24.9)"
                "OVERWEIGHT" -> "น้ำหนักเกิน (BMI 25-29.9)"
                "OBESE" -> "โรคอ้วน (BMI ≥ 30)"
                else -> "ไม่ทราบช่วง BMI"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // รายการโปรแกรม
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(programs) { program ->
                // แสดงการ์ดโปรแกรม
                ExerciseProgramCard(program = program)
            }
        }

        // ปุ่ม "กลับ"
        ElevatedButton(
            onClick = onBackPressed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "กลับ",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// คอมโพเนนต์สำหรับแสดงการ์ดโปรแกรมออกกำลังกาย
@Composable
fun ExerciseProgramCard(program: ExerciseProgram) {
    // สถานะการขยายการ์ด
    var expanded by remember { mutableStateOf(false) }
    // การ์ด
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // ชื่อโปรแกรม
            Text(
                text = program.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // แสดงรายละเอียดโปรแกรมเมื่อขยาย
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                program.exercises.forEach { exercise ->
                    // ชื่อและระยะเวลาของท่า
                    Text(
                        text = "${exercise.name} - ${exercise.duration}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    // คำอธิบายท่า
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}