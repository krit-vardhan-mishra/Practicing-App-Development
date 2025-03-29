package com.just_for_fun.mycalculator

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.lang.Double.parseDouble
import kotlin.math.*

class AnotherActivity : AppCompatActivity() {

    // Resultant Strings
    private var firstNumber : String = ""
    private var secondNumber : String = ""
    private var operator : String? = null

    // Input TextViews
    private lateinit var firstInput : TextView
    private lateinit var resultView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_another)

        // Initializing Input TextViews
        firstInput = findViewById(R.id.first_input)
        resultView = findViewById(R.id.result)

        numbersButton()
        operatorsButton()
    }

    private fun numbersButton() {

        val numberButtons = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_dot
        )

        for (id in numberButtons) {
            findViewById<AppCompatButton>(id).setOnClickListener { view ->
                fillInput((view as AppCompatButton).text.toString())
            }
        }
    }

    private fun operatorsButton() {
        val operatorButtons = mapOf (
            R.id.button_add to "+",
            R.id.button_sub to "-",
            R.id.button_mul to "*",
            R.id.button_div to "/",
            R.id.button_power to "^",
        )

        for ((id, op) in operatorButtons) {
            findViewById<AppCompatButton>(id).setOnClickListener {
                handleOperatorInput(op)
            }
        }

        findViewById<AppCompatButton>(R.id.button_equal).setOnClickListener { performEqualTo() }
        findViewById<AppCompatButton>(R.id.button_clear).setOnClickListener { performClear() }
        findViewById<AppCompatButton>(R.id.button_del).setOnClickListener { performDelete() }
        findViewById<AppCompatButton>(R.id.button_root).setOnClickListener { performRoot() }
        findViewById<AppCompatButton>(R.id.button_pie).setOnClickListener { fillInput(PI.toString()) }
        findViewById<AppCompatButton>(R.id.button_sin).setOnClickListener { performTrigonometry("sin") }
        findViewById<AppCompatButton>(R.id.button_cos).setOnClickListener { performTrigonometry("cos") }
        findViewById<AppCompatButton>(R.id.button_tan).setOnClickListener { performTrigonometry("tan") }
    }

    private fun fillInput(input : String) {
        if (operator == null) {
            firstNumber += input
            resultView.text = firstNumber
        } else {
            secondNumber += input
            resultView.text = secondNumber
        }
    }

    private fun handleOperatorInput(opt : String) {
        if (firstNumber.isNotEmpty()) {
            operator = opt
            firstInput.text = "$firstNumber $operator"
            resultView.text = operator
        }
    }

    private fun performRoot() {
        if (firstNumber.isNotEmpty()) {
            val result = sqrt(firstNumber.toDouble())
            displayResult(result)
        }
    }

    private fun performTrigonometry(type : String) {
        if (firstNumber.isNotEmpty()) {
            val radians = (firstNumber.toDouble())
            val result = when (type) {
                "sin" -> sin(radians)
                "cos" -> cos(radians)
                "tan" -> tan(radians)
                else -> 0.0

            }
            displayResult(result)
        }
    }

    private fun performClear() {
        firstNumber = ""
        secondNumber = ""
        operator = null
        firstInput.text = ""
        resultView.text = ""
    }

    private fun performDelete() {
        if (operator == null && firstNumber.isNotEmpty()) {
            firstNumber = firstNumber.dropLast(1)
            firstInput.text = firstNumber
        } else if (operator != null && secondNumber.isNotEmpty()) {
            secondNumber = secondNumber.dropLast(1)
            resultView.text = secondNumber
        }
    }

    private fun performEqualTo() {
        if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && operator != null) {
            val first = parseDouble(firstNumber)
            val second = parseDouble(secondNumber)
            val result = when (operator) {
                "+" -> first + second
                "-" -> first - second
                "*" -> first * second
                "/" -> first / second
                "^" -> first.pow(second)
                else -> 0.0
            }

            displayResult(result)
        }
    }

    private fun displayResult(result : Double) {
        val formattedResult = formatResult(result)
        firstNumber = formattedResult
        secondNumber = ""
        operator = null
        firstInput.text = firstInput.text.toString() + " " + firstNumber
        resultView.text = formattedResult
    }

    private fun formatResult(result: Double): String {
        return if (result % 1.0 == 0.0) {
            result.toInt().toString()
        } else {
            String.format("%f", result)
        }
    }
}
