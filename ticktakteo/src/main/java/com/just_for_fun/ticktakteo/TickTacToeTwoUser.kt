package com.just_for_fun.ticktakteo

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TickTacToeTwoUser : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private var currentPlayer = "X"
    private var gameBoard = Array(9) { "" }
    private var gameOver = false
    private val board = Array(3) { CharArray(3) {' '} }
    private var winningLine: Path? = null
    private lateinit var scoreBoard: TextView
    private var userOneScore = 0
    private var userTwoScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick_tak_toe)

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
        scoreBoard = findViewById(R.id.score_board)

        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                scoreBoard.text = "User 1: $userOneScore  |   User 2: $userTwoScore"
                onCellClick(i)
                scoreBoard.text = "User 1: $userOneScore  |   User 2: $userTwoScore"
            }
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            resetGame()
        }

        val homeButton : Button = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this ,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun onCellClick(index: Int) {
        if (buttons[index].isEnabled.not() || gameOver) {
            return
        }

        gameBoard[index] = currentPlayer
        buttons[index].text = currentPlayer
        buttons[index].isEnabled = false

        if (checkForWinner()) {
            gameOver = true
            statusText.text = "$currentPlayer Wins!"
            Toast.makeText(this, "$currentPlayer Wins!", Toast.LENGTH_LONG).show()
            highlightWinningLine()
            Handler(Looper.getMainLooper()).postDelayed({ resetGame() }, 2000)
            return
        }

        if (gameBoard.all { it.isNotEmpty() }) {
            gameOver = true
            statusText.text = "It's a Draw!"
            Toast.makeText(this, "It's a Draw!", Toast.LENGTH_LONG).show()
            Handler(Looper.getMainLooper()).postDelayed({ resetGame() }, 2000)
            return
        }

        currentPlayer = if (currentPlayer == "X") "O" else "X"
        statusText.text = "Player $currentPlayer's Turn"

        if (currentPlayer == "O") {
            val (bestRow, bestCol) = findBestMove()
            val bestIndex = bestRow * 3 + bestCol
            onCellClick(bestIndex)
        }
    }

    private fun checkForWinner(): Boolean {
        val winPatterns = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5),
            arrayOf(6, 7, 8), arrayOf(0, 3, 6),
            arrayOf(1, 4, 7), arrayOf(2, 5, 8),
            arrayOf(0, 4, 8), arrayOf(2, 4, 6)
        )

        for (pattern in winPatterns) {
            val (a, b, c) = pattern
            if (gameBoard[a] == gameBoard[b] && gameBoard[b] == gameBoard[c] && gameBoard[a].isNotEmpty()) {
                winningLine = Path().apply {
                    moveTo(buttons[a].x + buttons[a].width / 2, buttons[a].y + buttons[a].height / 2)
                    lineTo(buttons[c].x + buttons[c].width / 2, buttons[c].y + buttons[c].height / 2)
                }
                return true
            }
        }

        return false
    }

    private fun highlightWinningLine() {
        val canvas = Canvas()
        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 10f
            style = Paint.Style.STROKE
        }

        winningLine?.let {
            canvas.drawPath(it, paint)
        }
    }

    private fun minimax(board: Array<CharArray>, depth: Int, isMaximizing: Boolean): Int {
        val score = evaluate(board)

        if (score == 10) return score - depth
        if (score == -10) return score + depth
        if (!isMovesLeft(board)) return 0

        return if (isMaximizing) {
            var best = -1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O'
                        best = maxOf(best, minimax(board, depth + 1, false))
                        board[i][j] = ' '
                    }
                }
            }
            best
        } else {
            var best = 1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'X'
                        best = minOf(best, minimax(board, depth + 1, true))
                        board[i][j] = ' '
                    }
                }
            }
            best
        }
    }

    private fun evaluate(board: Array<CharArray>): Int {
        for (row in 0..2) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == 'O') return 10
                else if (board[row][0] == 'X') return -10
            }
        }

        for (col in 0..2) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                if (board[0][col] == 'O') return 10
                else if (board[0][col] == 'X') return -10
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == 'O') return 10
            else if (board[0][0] == 'X') return -10
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == 'O') return 10
            else if (board[0][2] == 'X') return -10
        }

        return 0
    }

    private fun isMovesLeft(board: Array<CharArray>): Boolean {
        for (i in 0..2)
            for (j in 0..2)
                if (board[i][j] == ' ')
                    return true
        return false
    }

    private fun findBestMove(): Pair<Int, Int> {
        var bestVal = -1000
        var bestMove = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O'
                    val moveVal = minimax(board, 0, false)
                    board[i][j] = ' '

                    if (moveVal > bestVal) {
                        bestMove = Pair(i, j)
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }

    private fun resetGame() {
        gameBoard.fill("")
        for (button in buttons) {
            button.text = ""
            button.isEnabled = true
        }
        gameOver = false
        currentPlayer = "X"
        statusText.text = "Player $currentPlayer's Turn"
        winningLine = null
        userOneScore = 0
        userTwoScore = 0
        scoreBoard.text = "User: $userOneScore  |   Ai: $userTwoScore"
    }
}
