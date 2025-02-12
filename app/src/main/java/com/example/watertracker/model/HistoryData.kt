package com.example.watertracker.model

data class HistoryData(
    val amount: Double = 0.0,
    val date: Long = 0
){
    constructor() : this(0.0, 0)
}