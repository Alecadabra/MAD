package com.alec.mad.p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val num1Button: EditText = findViewById(R.id.num1)
    private val num2Button: EditText = findViewById(R.id.num2)

    private val add: Button = findViewById(R.id.add)
    private val subtract: Button = findViewById(R.id.subtract)
    private val multiply: Button = findViewById(R.id.multiply)
    private val divide: Button = findViewById(R.id.divide)

    private val result: TextView = findViewById(R.id.result)

    private val num1: Double?
        get() = num1Button.text.toString().toDoubleOrNull()
    private val num2: Double?
        get() = num2Button.text.toString().toDoubleOrNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        add.setOnClickListener { this.computeResult { n1, n2 -> n1 + n2 } }
        subtract.setOnClickListener { this.computeResult { n1, n2 -> n1 - n2 } }
        multiply.setOnClickListener { this.computeResult { n1, n2 -> n1 * n2 } }
        divide.setOnClickListener { this.computeResult { n1, n2 -> n1 / n2 } }
    }

    private fun computeResult(operator: (Double, Double) -> Double) {
        result.text =
            if (num1 == null || num2 == null) "Error"
            else operator(num1!!, num2!!).toString()
    }
}