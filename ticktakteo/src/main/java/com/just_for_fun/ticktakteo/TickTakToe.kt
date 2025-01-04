package com.just_for_fun.ticktakteo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TickTakToe : AppCompatActivity() {

    // Buttons for the Tic-Tac-Toe grid
    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private var currentPlayer = "X"
    private var gameBoard = Array(9) { "" }
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick_tak_toe)

        // Initialize buttons
        buttons = arrayOf(
            findViewById(R.id.button_1),
            findViewById(R.id.button_2),
            findViewById(R.id.button_3),
            findViewById(R.id.button_4),
            findViewById(R.id.button_5),
            findViewById(R.id.button_6),
            findViewById(R.id.button_7),
            findViewById(R.id.button_8),
            findViewById(R.id.button_9)
        )

        statusText = findViewById(R.id.title)

        // Set up onClick listeners for all buttons
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                onCellClick(i)
            }
        }

        // Reset the game when the reset button is clicked
        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            resetGame()
        }
    }

    // Handle player move when a cell is clicked
    private fun onCellClick(index: Int) {
        if (buttons[index].isEnabled.not() || gameOver) {
            return  // Ignore if the cell is already filled or the game is over
        }

        // Mark the cell with the current player's symbol
        gameBoard[index] = currentPlayer
        buttons[index].text = currentPlayer
        buttons[index].isEnabled = false  // Disable the button after click

        // Check for a winner
        if (checkForWinner()) {
            gameOver = true
            statusText.text = "$currentPlayer Wins!"
            Toast.makeText(this, "$currentPlayer Wins!", Toast.LENGTH_LONG).show()
            return
        }

        // Check for a draw
        if (gameBoard.all { it.isNotEmpty() }) {
            gameOver = true
            statusText.text = "It's a Draw!"
            Toast.makeText(this, "It's a Draw!", Toast.LENGTH_LONG).show()
            return
        }

        // Switch to the next player
        currentPlayer = if (currentPlayer == "X") "O" else "X"
        statusText.text = "Player $currentPlayer's Turn"
    }

    // Check if there's a winner
    private fun checkForWinner(): Boolean {
        val winPatterns = arrayOf(
            // Rows
            arrayOf(0, 1, 2),
            arrayOf(3, 4, 5),
            arrayOf(6, 7, 8),
            // Columns
            arrayOf(0, 3, 6),
            arrayOf(1, 4, 7),
            arrayOf(2, 5, 8),
            // Diagonals
            arrayOf(0, 4, 8),
            arrayOf(2, 4, 6)
        )

        // Check if any winning pattern has the same symbol (X or O)
        for (pattern in winPatterns) {
            val (a, b, c) = pattern
            if (gameBoard[a] == gameBoard[b] && gameBoard[b] == gameBoard[c] && gameBoard[a].isNotEmpty()) {
                return true
            }
        }

        return false
    }

    // Reset the game to initial state
    private fun resetGame() {
        gameBoard.fill("")
        for (button in buttons) {
            button.text = ""
            button.isEnabled = true
        }
        gameOver = false
        currentPlayer = "X"
        statusText.text = "Player $currentPlayer's Turn"
    }
}
