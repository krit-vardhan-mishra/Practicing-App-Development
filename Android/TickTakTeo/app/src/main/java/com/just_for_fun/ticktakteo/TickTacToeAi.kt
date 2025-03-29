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

class TickTacToeAI : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private val humanPlayer = "X"
    private val aiPlayer = "O"
    private var gameBoard = Array(9) { "" }
    private var gameOver = false
    private val board = Array(3) { CharArray(3) { ' ' } }
    private var winningLine: Path? = null
    private var userScore: Int = 0
    private var aiScore: Int = 0
    private lateinit var scoreBoard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick_tak_toe)

        buttons = arrayOf(
            findViewById(R.id.button_1), findViewById(R.id.button_2), findViewById(R.id.button_3),
            findViewById(R.id.button_4), findViewById(R.id.button_5), findViewById(R.id.button_6),
            findViewById(R.id.button_7), findViewById(R.id.button_8), findViewById(R.id.button_9)
        )

        statusText = findViewById(R.id.title)
        scoreBoard = findViewById(R.id.score_board)
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
        statusText.text = "Your Turn (X)"

        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                onHumanMove(i)
                scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
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

    private fun onHumanMove(index: Int) {
        if (buttons[index].isEnabled.not() || gameOver) {
            return
        }

        // Update the game state with human move
        makeMove(index, humanPlayer)

        if (checkForWinner()) {
            endGame("You Win!")
            userScore++
            return
        }

        if (isBoardFull()) {
            endGame("It's a Draw!")
            return
        }

        // AI's turn
        statusText.text = "AI is thinking..."
        Handler(Looper.getMainLooper()).postDelayed({
            makeAIMove()
        }, 500) // Add small delay to make AI move feel more natural
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
    }

    private fun makeAIMove() {
        // Convert gameBoard to 3x3 board for minimax
        updateBoardArray()

        val (bestRow, bestCol) = findBestMove()
        val bestIndex = bestRow * 3 + bestCol

        makeMove(bestIndex, aiPlayer)

        if (checkForWinner()) {
            endGame("AI Wins!")
            aiScore++
            return
        }

        if (isBoardFull()) {
            endGame("It's a Draw!")
            return
        }

        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
        statusText.text = "Your Turn (X)"
    }

    private fun makeMove(index: Int, player: String) {
        gameBoard[index] = player
        buttons[index].text = player
        buttons[index].isEnabled = false

        // Update the board array for minimax
        val row = index / 3
        val col = index % 3
        board[row][col] = if (player == humanPlayer) 'X' else 'O'
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
    }

    private fun minimax(board: Array<CharArray>, depth: Int, isMaximizing: Boolean): Int {
        val score = evaluate(board)

        // Terminal conditions
        if (score == 10) return score - depth  // AI wins
        if (score == -10) return score + depth // Human wins
        if (!isMovesLeft(board)) return 0      // Draw

        return if (isMaximizing) {
            var bestScore = -1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O'  // AI move
                        bestScore = maxOf(bestScore, minimax(board, depth + 1, false))
                        board[i][j] = ' '  // Undo move
                    }
                }
            }
            bestScore
        } else {
            var bestScore = 1000
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'X'  // Human move
                        bestScore = minOf(bestScore, minimax(board, depth + 1, true))
                        board[i][j] = ' '  // Undo move
                    }
                }
            }
            bestScore
        }
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
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
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
        return bestMove
    }

    private fun evaluate(board: Array<CharArray>): Int {
        // Check rows
        for (row in 0..2) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                when (board[row][0]) {
                    'O' -> return 10  // AI wins
                    'X' -> return -10 // Human wins
                }
            }
        }

        // Check columns
        for (col in 0..2) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                when (board[0][col]) {
                    'O' -> return 10
                    'X' -> return -10
                }
            }
        }

        // Check diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            when (board[0][0]) {
                'O' -> return 10
                'X' -> return -10
            }
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            when (board[0][2]) {
                'O' -> return 10
                'X' -> return -10
            }
        }

        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
        return 0 // No winner
    }

    private fun isMovesLeft(board: Array<CharArray>): Boolean {
        return board.any { row -> row.any { it == ' ' } }
    }

    private fun isBoardFull(): Boolean {
        return gameBoard.none { it.isEmpty() }
    }

    private fun updateBoardArray() {
        for (i in 0..2) {
            for (j in 0..2) {
                val index = i * 3 + j
                board[i][j] = when (gameBoard[index]) {
                    humanPlayer -> 'X'
                    aiPlayer -> 'O'
                    else -> ' '
                }
            }
        }
    }

    private fun checkForWinner(): Boolean {
        val winPatterns = arrayOf(
            arrayOf(0, 1, 2),
            arrayOf(3, 4, 5),
            arrayOf(6, 7, 8),
            arrayOf(0, 3, 6),
            arrayOf(1, 4, 7),
            arrayOf(2, 5, 8),
            arrayOf(0, 4, 8),
            arrayOf(2, 4, 6)
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

    private fun endGame(message: String) {
        gameOver = true
        statusText.text = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        highlightWinningLine()
        Handler(Looper.getMainLooper()).postDelayed({ resetGame() }, 2000)
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

    private fun resetGame() {
        gameBoard.fill("")
        for (button in buttons) {
            button.text = ""
            button.isEnabled = true
        }
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = ' '
            }
        }
        gameOver = false
        statusText.text = "Your Turn (X)"
        winningLine = null
        userScore = 0
        aiScore = 0
        scoreBoard.text = "User: $userScore  |   Ai: $aiScore"
    }
}
