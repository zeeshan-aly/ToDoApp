package com.example.todoapp.domain.model

import java.util.Date

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Date? = null,
    val isDone: Boolean = false,
    val createdAt: Date = Date()
)
