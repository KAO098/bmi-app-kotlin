package com.example.myapplication2

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    // ใช้ mutableStateListOf สำหรับการอัปเดต UI
    val messageList = mutableStateListOf<MessageModel>()

    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constants.apikey
    )

    // เก็บ chat instance ไว้ใช้ต่อเนื่อง
    private var currentChat = generativeModel.startChat()

    // ฟังก์ชันในการส่งข้อความและรับคำตอบจากโมเดล
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing........", "model"))

                val response = currentChat.sendMessage(question)
                messageList.removeLast() // ลบ "Typing........" เมื่อได้รับการตอบกลับ
                response.text?.let { responseText ->
                    messageList.add(MessageModel(responseText, "bot"))
                    Log.i("ChatViewModel", "Response: $responseText")
                }

            } catch (e: Exception) {
                messageList.removeLast() // ลบ "Typing........" เมื่อเกิดข้อผิดพลาด
                Log.e("ChatViewModel", "Error in sendMessage", e)
                messageList.add(MessageModel("Sorry, an error occurred. Please try again.", "bot"))
            }
        }
    }
}