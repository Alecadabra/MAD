package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.model.Question

class Flag(
    val name: String,
    val questions: List<Question>,
    val drawableId: Int,
    var enabled: Boolean = true
)