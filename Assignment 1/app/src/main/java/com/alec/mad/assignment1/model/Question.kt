package com.alec.mad.assignment1.model

class Question(
    val points: Int,
    val penalty: Int,
    val isSpecial: Boolean,
    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String,
    var enabled: Boolean = true
)