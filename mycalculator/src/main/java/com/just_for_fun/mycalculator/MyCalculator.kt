package com.just_for_fun.mycalculator

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class MyCalculator : AppCompatActivity() {

    // For checking
    private var isUsed : Boolean = false
    private var isTrigonometry : Boolean = false

    // Resultant Strings
    private var operandOne : String = ""
    private var operandTwo : String = ""
    private var operator : String? = null
    private var result : String = ""

    // Input TextViews
    private lateinit var firstInput : TextView
    private lateinit var secondInput : TextView
    private lateinit var resultView : TextView

    // Number Buttons
    private lateinit var buttonOne : AppCompatButton
    private lateinit var buttonTwo : AppCompatButton
    private lateinit var buttonThree : AppCompatButton
    private lateinit var buttonFour : AppCompatButton
    private lateinit var buttonFive : AppCompatButton
    private lateinit var buttonSix : AppCompatButton
    private lateinit var buttonSeven : AppCompatButton
    private lateinit var buttonEight : AppCompatButton
    private lateinit var buttonNine : AppCompatButton
    private lateinit var buttonZero : AppCompatButton
    private lateinit var buttonDot : AppCompatButton

    // Operator Buttons
    private lateinit var addition : AppCompatButton
    private lateinit var subtraction : AppCompatButton
    private lateinit var multiplication : AppCompatButton
    private lateinit var division : AppCompatButton

    // Function Buttons
    private lateinit var power : AppCompatButton
    private lateinit var pie : AppCompatButton
    private lateinit var root : AppCompatButton

    // Trigonometric Buttons
    private lateinit var sin : AppCompatButton
    private lateinit var cos : AppCompatButton
    private lateinit var tan : AppCompatButton

    // Special Buttons
    private lateinit var delete : AppCompatButton
    private lateinit var equal : AppCompatButton
    private lateinit var clear : AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_calculator)

        // Initializing Input TextViews
        firstInput = findViewById(R.id.first_input)
        secondInput = findViewById(R.id.second_input)
        resultView = findViewById(R.id.result)

        // Initializing Number Buttons
        buttonOne = findViewById(R.id.button_1)
        buttonTwo = findViewById(R.id.button_2)
        buttonThree = findViewById(R.id.button_3)
        buttonFour = findViewById(R.id.button_4)
        buttonFive = findViewById(R.id.button_5)
        buttonSix = findViewById(R.id.button_6)
        buttonSeven = findViewById(R.id.button_7)
        buttonEight= findViewById(R.id.button_8)
        buttonNine = findViewById(R.id.button_9)
        buttonZero = findViewById(R.id.button_0)
        buttonDot = findViewById(R.id.button_dot)

        // Operator Initializing
        addition = findViewById(R.id.button_add)
        subtraction = findViewById(R.id.button_sub)
        multiplication = findViewById(R.id.button_mul)
        division = findViewById(R.id.button_div)

        // Function Initializing
        power = findViewById(R.id.button_power)
        pie = findViewById(R.id.button_pie)
        root = findViewById(R.id.button_root)

        // Trigonometry Initializing
        sin = findViewById(R.id.button_sin)
        cos = findViewById(R.id.button_cos)
        tan = findViewById(R.id.button_tan)

        // Special Button Initializing
        clear = findViewById(R.id.button_clear)
        delete = findViewById(R.id.button_del)
        equal = findViewById(R.id.button_equal)

        calculator()
    }

    private fun calculator() {
        buttonOne.setOnClickListener {
            fillInput("1")
        }

        buttonTwo.setOnClickListener {
            fillInput("2")
        }

        buttonThree.setOnClickListener {
            fillInput("3")
        }

        buttonFour.setOnClickListener {
            fillInput("4")
        }

        buttonFive.setOnClickListener {
            fillInput("5")
        }

        buttonSix.setOnClickListener {
            fillInput("6")
        }

        buttonSeven.setOnClickListener {
            fillInput("7")
        }

        buttonEight.setOnClickListener {
            fillInput("8")
        }

        buttonNine.setOnClickListener {
            fillInput("9")
        }

        buttonZero.setOnClickListener {
            fillInput("0")
        }

        buttonDot.setOnClickListener {
            fillInput(".")
        }

        addition.setOnClickListener {
            operatorCall("add")
        }

        subtraction.setOnClickListener {
            operatorCall("sub")
        }

        multiplication.setOnClickListener {
            operatorCall("mul")
        }

        division.setOnClickListener {
            operatorCall("div")
        }

        power.setOnClickListener {
            operatorCall("pow")
        }

        sin.setOnClickListener {
            performTrigonometry("sin")
        }

        cos.setOnClickListener {
            performTrigonometry("cos")
        }

        tan.setOnClickListener {
            performTrigonometry("tan")
        }

        equal.setOnClickListener {
            performEqualTo()
        }

        clear.setOnClickListener {
            performClear()
        }

        delete.setOnClickListener {
            performDelete()
        }
    }


    private fun fillInput(num : String) {
        if (isUsed) {
             when (num) {
                 "0" -> operandTwo += 0
                 "1" -> operandTwo += 1
                 "2" -> operandTwo += 2
                 "3" -> operandTwo += 3
                 "4" -> operandTwo += 4
                 "5" -> operandTwo += 5
                 "6" -> operandTwo += 6
                 "7" -> operandTwo += 7
                 "8" -> operandTwo += 8
                 "9" -> operandTwo += 9
                 "." -> operandTwo += "."
             }
        } else {
            when (num) {
                "0" -> operandOne += 0
                "1" -> operandOne += 1
                "2" -> operandOne += 2
                "3" -> operandOne += 3
                "4" -> operandOne += 4
                "5" -> operandOne += 5
                "6" -> operandOne += 6
                "7" -> operandOne += 7
                "8" -> operandOne += 8
                "9" -> operandOne += 9
                "." -> operandOne += "."
            }
            isUsed = true
        }
    }

    private fun operatorCall(opt : String) {
        when (opt) {
            "add" -> operator = "+"
            "sub" -> operator = "-"
            "mul" -> operator = "*"
            "div" -> operator = "/"
            "pow" -> operator = "^"
        }
    }

    private fun performTrigonometry(str : String) {
        if (!isUsed) {
            when (str) {
                "sin" -> trigonometryOperation("sin")
                "cos" -> trigonometryOperation("cos")
                "tan" -> trigonometryOperation("tan")
            }
        } else {
            operator = str
        }
    }

    private fun trigonometryOperation(str : String) {
        operandOne = "0"
        operator = str
        isTrigonometry = true
    }

    private fun performClear() {
        result = ""
        operator = ""
        isUsed = false
        operandOne = ""
        operandTwo = ""
        operandOne = ""
        resultView.text = ""
        firstInput.text = ""
        secondInput.text = ""
    }

    private fun performDelete() {
        if (isUsed) {
            operandTwo = operandTwo.dropLast(1)
            secondInput.text = operandTwo
        } else {
            operandOne = operandOne.dropLast(1)
            firstInput.text = operandOne
        }
    }

    private fun performEqualTo() {
        if (isUsed && !isTrigonometry) {
            if (operator != "/") {
                result = (parseInt(operandOne) + parseInt(operator) + parseInt(operandTwo)).toString()
                resultView.text = result
                operandOne = result
            } else {
                try {
                    result = (parseInt(operandOne) + parseInt(operator) + parseInt(operandTwo)).toString()
                    resultView.text = result
                    operandOne = result
                } catch (e : Exception) {
                    resultView.text = "∞"
                }
            }
        } else if (isTrigonometry) {
            val angleDegree = operandTwo
            val angleRadians = Math.toRadians(parseDouble(angleDegree))

            when (operator) {
                "sin" -> result = Math.sin(angleRadians).toString()
                "cos" -> result = Math.cos(angleRadians).toString()
                "tan" -> result = Math.tan(angleRadians).toString()
            }

            resultView.text = result
        }
        else {
            return
        }
    }
}
