package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons

class NutritionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutritionScreen()
        }
    }
}

// Color Scheme
val customColorScheme = lightColorScheme(
    primary = Color(0xFF006494),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCBE6FF),
    onPrimaryContainer = Color(0xFF001E32),
    secondary = Color(0xFF5D5B70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE3E0F9),
    onSecondaryContainer = Color(0xFF191927),
    tertiary = Color(0xFF795548),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD7CCC8),
    onTertiaryContainer = Color(0xFF282828),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen() {
    MaterialTheme(colorScheme = customColorScheme) {
        var selectedBmiCategory by remember { mutableStateOf<String?>(null) }
        var isCardVisible by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "โภชนาการ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "เลือกช่วง BMI ของคุณเพื่อดูคำแนะนำ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val buttonModifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)

                    ElevatedButton(
                        onClick = {
                            if (selectedBmiCategory == "Underweight") {
                                isCardVisible = !isCardVisible
                            } else {
                                selectedBmiCategory = "Underweight"
                                isCardVisible = true
                            }
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("BMI < 18.5 (น้ำหนักน้อย / ผอม)", style = MaterialTheme.typography.titleMedium)
                    }
                    ElevatedButton(
                        onClick = {
                            if (selectedBmiCategory == "Normal") {
                                isCardVisible = !isCardVisible
                            } else {
                                selectedBmiCategory = "Normal"
                                isCardVisible = true
                            }
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("BMI 18.5 - 22.9 (ปกติ)", style = MaterialTheme.typography.titleMedium)
                    }
                    ElevatedButton(
                        onClick = {
                            if (selectedBmiCategory == "Overweight1") {
                                isCardVisible = !isCardVisible
                            } else {
                                selectedBmiCategory = "Overweight1"
                                isCardVisible = true
                            }
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("BMI 23.0 - 24.9 (ท้วม)", style = MaterialTheme.typography.titleMedium)
                    }
                    ElevatedButton(
                        onClick = {
                            if (selectedBmiCategory == "Overweight2") {
                                isCardVisible = !isCardVisible
                            } else {
                                selectedBmiCategory = "Overweight2"
                                isCardVisible = true
                            }
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("BMI 25.0 - 29.9 (อ้วน)", style = MaterialTheme.typography.titleMedium)
                    }
                    ElevatedButton(
                        onClick = {
                            if (selectedBmiCategory == "Obese") {
                                isCardVisible = !isCardVisible
                            } else {
                                selectedBmiCategory = "Obese"
                                isCardVisible = true
                            }
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("BMI ≥ 30.0 (อ้วนมาก)", style = MaterialTheme.typography.titleMedium)
                    }
                }

                AnimatedVisibility(
                    visible = isCardVisible && selectedBmiCategory != null,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                            expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(durationMillis = 500)
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500)) +
                            shrinkVertically(
                                shrinkTowards = Alignment.Top,
                                animationSpec = tween(durationMillis = 500)
                            )
                ) {
                    NutritionPlanCard(bmiCategory = selectedBmiCategory!!)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NutritionPlanCard(bmiCategory: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)

        ) {
            Text(
                text = "แผนโภชนาการสำหรับ ${getBmiCategoryName(bmiCategory)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            when (bmiCategory) {
                "Underweight" -> {
                    NutritionText("เป้าหมาย: เพิ่มน้ำหนัก", fontWeight = FontWeight.Bold)
                    NutritionText("คำแนะนำ:")
                    NutritionText("- ควรได้รับพลังงานเพิ่มขึ้นประมาณ 300-500 กิโลแคลอรีต่อวันจาก TDEE")
                    NutritionText("- แนะนำให้เพิ่มปริมาณคาร์โบไฮเดรตเชิงซ้อน โปรตีน และไขมันดี")
                    NutritionText("- เน้นอาหารที่มีสารอาหารหนาแน่น เช่น ธัญพืชเต็มเมล็ด เนื้อสัตว์ไม่ติดมัน ปลา ถั่ว เมล็ดพืช และอะโวคาโด")
                    NutritionText("- รับประทานอาหารให้บ่อยขึ้น อาจเป็นมื้อเล็ก ๆ 5-6 มื้อต่อวัน")
                    NutritionText("- ดื่มเครื่องดื่มที่มีแคลอรีสูง เช่น นมปั่น สมูทตี้")
                    NutritionText("ตัวอย่างอาหาร:", fontWeight = FontWeight.Bold)
                    NutritionText("- เช้า: ข้าวโอ๊ตใส่นม อัลมอนด์ และผลไม้, ไข่ต้ม 2 ฟอง")
                    NutritionText("- กลางวัน: ข้าวกล้อง, อกไก่ย่าง, ผัดผักรวม")
                    NutritionText("- เย็น: สลัดปลาทูน่า, ควินัว, อะโวคาโด")
                    NutritionText("- ของว่าง: โยเกิร์ต, กล้วย, ถั่วรวม")
                }
                "Normal" -> {
                    NutritionText("เป้าหมาย: รักษาน้ำหนัก", fontWeight = FontWeight.Bold)
                    NutritionText("คำแนะนำ:")
                    NutritionText("- รับประทานอาหารให้สมดุลและหลากหลาย")
                    NutritionText("- เน้นผัก ผลไม้ ธัญพืชเต็มเมล็ด โปรตีนไขมันต่ำ และไขมันดี")
                    NutritionText("- ควบคุมปริมาณอาหารให้เหมาะสม")
                    NutritionText("- ควรได้รับพลังงานให้เท่ากับ TDEE ในแต่ละวัน")
                    NutritionText("- ดื่มน้ำเปล่าให้เพียงพอ")
                    NutritionText("- ออกกำลังกายอย่างสม่ำเสมอ")
                    NutritionText("ตัวอย่างอาหาร:", fontWeight = FontWeight.Bold)
                    NutritionText("- เช้า: โจ๊กข้าวโอ๊ต, ไข่ต้ม 1 ฟอง")
                    NutritionText("- กลางวัน: ข้าวกล้อง, ปลาแซลมอนย่าง, สลัดผัก")
                    NutritionText("- เย็น: ซุปผัก, อกไก่อบ, มันหวานอบ")
                    NutritionText("- ของว่าง: ผลไม้, โยเกิร์ตไขมันต่ำ")
                }
                "Overweight1" -> {
                    NutritionText("เป้าหมาย: ลดน้ำหนักช้าๆ", fontWeight = FontWeight.Bold)
                    NutritionText("คำแนะนำ:")
                    NutritionText("- ลดพลังงานลงประมาณ 250 กิโลแคลอรีต่อวันจาก TDEE")
                    NutritionText("- ลดปริมาณคาร์โบไฮเดรตและไขมัน")
                    NutritionText("- เพิ่มการรับประทานโปรตีนและไฟเบอร์")
                    NutritionText("- เน้นผักใบเขียว ผลไม้ไม่หวาน ธัญพืชไม่ขัดสี")
                    NutritionText("- เลือกวิธีการปรุงอาหาร เช่น ต้ม นึ่ง ย่าง แทนการทอด")
                    NutritionText("- ดื่มน้ำเปล่าให้มากขึ้น")
                    NutritionText("ตัวอย่างอาหาร:", fontWeight = FontWeight.Bold)
                    NutritionText("- เช้า: ไข่คนผักโขม, ขนมปังโฮลวีท 1 แผ่น")
                    NutritionText("- กลางวัน: สลัดอกไก่, น้ำสลัดใส")
                    NutritionText("- เย็น: ปลานึ่ง, ผักต้ม")
                    NutritionText("- ของว่าง: แอปเปิ้ล, อัลมอนด์ 1 กำมือ")
                }
                "Overweight2" -> {
                    NutritionText("เป้าหมาย: ลดน้ำหนัก", fontWeight = FontWeight.Bold)
                    NutritionText("คำแนะนำ:")
                    NutritionText("- ลดพลังงานลงประมาณ 500 กิโลแคลอรีต่อวันจาก TDEE")
                    NutritionText("- เน้นผัก ผลไม้ไม่หวาน โปรตีนไขมันต่ำ")
                    NutritionText("- ลดคาร์โบไฮเดรตขัดสี เช่น ข้าวขาว ขนมปังขาว")
                    NutritionText("- หลีกเลี่ยงอาหารแปรรูป อาหารทอด อาหารที่มีไขมันสูง")
                    NutritionText("- ดื่มน้ำเปล่าก่อนมื้ออาหาร")
                    NutritionText("- เพิ่มการออกกำลังกาย")
                    NutritionText("ตัวอย่างอาหาร:", fontWeight = FontWeight.Bold)
                    NutritionText("- เช้า: สมูทตี้ผัก, ไข่ต้ม 1 ฟอง")
                    NutritionText("- กลางวัน: สลัดทูน่า, ผักสด")
                    NutritionText("- เย็น: อกไก่ย่าง, บร็อคโคลี่นึ่ง")
                    NutritionText("- ของว่าง: แตงโม, โยเกิร์ตไขมันต่ำ")
                }
                "Obese" -> {
                    NutritionText("เป้าหมาย: ลดน้ำหนักอย่างจริงจัง", fontWeight = FontWeight.Bold)
                    NutritionText("คำแนะนำ:")
                    NutritionText("- ควรปรึกษานักโภชนาการหรือแพทย์เพื่อวางแผนการลดน้ำหนักที่เหมาะสม")
                    NutritionText("- ลดพลังงานลงประมาณ 750-1,000 กิโลแคลอรีต่อวันจาก TDEE")
                    NutritionText("- เน้นอาหารที่มีกากใยสูง โปรตีนสูง และไขมันต่ำ")
                    NutritionText("- ตัดอาหารที่มีน้ำตาลสูง ไขมันสูง อาหารแปรรูป")
                    NutritionText("- อาจพิจารณาการใช้ยาลดน้ำหนักหรือการผ่าตัดภายใต้คำแนะนำของแพทย์")
                    NutritionText("- ออกกำลังกายอย่างสม่ำเสมอตามความเหมาะสม")
                    NutritionText("ตัวอย่างอาหาร:", fontWeight = FontWeight.Bold)
                    NutritionText("- เช้า: ไข่ขาว 2 ฟอง, ผักลวก")
                    NutritionText("- กลางวัน: เกาเหลาอกไก่ไม่ใส่กระเทียมเจียว")
                    NutritionText("- เย็น: สลัดผัก, อกไก่นึ่ง")
                    NutritionText("- ของว่าง: ฝรั่ง, นมพร่องมันเนย")
                }
            }
        }
    }
}

@Composable
fun NutritionText(text: String, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = fontWeight),
        modifier = Modifier.padding(top = 4.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

fun getBmiCategoryName(category: String): String {
    return when (category) {
        "Underweight" -> "น้ำหนักน้อย / ผอม"
        "Normal" -> "ปกติ (สุขภาพดี)"
        "Overweight1" -> "ท้วม / โรคอ้วนระดับ 1"
        "Overweight2" -> "อ้วน / โรคอ้วนระดับ 2"
        "Obese" -> "อ้วนมาก / โรคอ้วนระดับ 3"
        else -> ""
    }
}