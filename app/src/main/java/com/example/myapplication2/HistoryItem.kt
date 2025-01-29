package com.example.myapplication2

data class HistoryItem(
    val id: Int,
    val bmi: Float,
    val category: String,
    val dailyCalorie: Float,
    val dateTime: String
){
    // ตรวจสอบว่าเป็นรายการเดียวกันหรือไม่
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HistoryItem) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}