package com.alec.mad.p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var num1Button: EditText
    private lateinit var num2Button: EditText

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num1Button = findViewById(R.id.num1Text)
        num2Button = findViewById(R.id.num2Text)

        val add: Button = findViewById(R.id.add)
        add.setOperation { n1, n2 -> n1 + n2 }
        val subtract: Button = findViewById(R.id.subtract)
        subtract.setOperation { n1, n2 -> n1 - n2 }
        val multiply: Button = findViewById(R.id.multiply)
        multiply.setOperation { n1, n2 -> n1 * n2 }
        val divide: Button = findViewById(R.id.divide)
        divide.setOperation { n1, n2 -> n1 / n2 }

        result = findViewById(R.id.result)
    }

    private fun Button.setOperation(operator: (Double, Double) -> Double) {
        this.setOnClickListener {
            val num1 = num1Button.text.toString().toDoubleOrNull()
            val num2 = num2Button.text.toString().toDoubleOrNull()
            result.text =
                if (num1 == null || num2 == null) "Error (Not a number)"
                else operator(num1, num2).toString()
        }
    }
}