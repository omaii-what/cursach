package com.example.cursach

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var size = 4
    private var boxRows = 2
    private var boxCols = 2
    private var level = 0

    private lateinit var solved: Array<IntArray>
    private lateinit var puzzle: Array<IntArray>
    private lateinit var fixed: Array<BooleanArray>

    private var selectedR = -1
    private var selectedC = -1
    private var errors = 0
    private var seconds = 0
    private var running = true

    private lateinit var tvTimer: TextView
    private lateinit var tvErrors: TextView
    private lateinit var board: SudokuBoardView

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            if (running) {
                seconds++
                tvTimer.text = "Время: ${Prefs.formatTime(seconds)}"
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        tvTimer = findViewById(R.id.tvTimer)
        tvErrors = findViewById(R.id.tvErrors)

        board = findViewById(R.id.board)
        board.onCellSelected = { r, c ->
            selectedR = r
            selectedC = c
        }

        if (savedInstanceState == null) {
            startNewGame()
        }

        findViewById<Button>(R.id.btnHint).setOnClickListener { showHint() }

        handler.postDelayed(tick, 1000)
    }

    private fun startNewGame() {
        size = Prefs.getSize(this)
        level = Prefs.getLevel(this)
        boxRows = 2
        boxCols = if (size == 4) 2 else 3

        solved = Sudoku.generateSolved(size)

        val total = size * size
        val emptyCount = when (level) {
            0 -> total / 3
            1 -> total / 2
            else -> total - 2
        }
        puzzle = Sudoku.makePuzzle(solved, size, emptyCount)
        fixed = Array(size) { r -> BooleanArray(size) { c -> puzzle[r][c] != 0 } }

        selectedR = -1
        selectedC = -1
        errors = 0
        seconds = 0
        running = true
        tvErrors.text = "Ошибки: 0"
        tvTimer.text = "Время: 00:00"

        board.setBoard(size, boxRows, boxCols, puzzle, fixed)

        buildNumberButtons()
    }

    private fun buildNumberButtons() {
        val row = findViewById<LinearLayout>(R.id.numbersRow)
        row.removeAllViews()
        for (n in 1..size) {
            val btn = Button(this)
            btn.text = n.toString()
            btn.layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            )
            btn.setOnClickListener { onNumber(n) }
            row.addView(btn)
        }
    }

    private fun onNumber(n: Int) {
        if (selectedR < 0) return
        if (fixed[selectedR][selectedC]) return

        puzzle[selectedR][selectedC] = n
        if (n != solved[selectedR][selectedC]) {
            errors++
            tvErrors.text = "Ошибки: $errors"
        }
        refreshBoard()
        checkWin()
    }

    private fun showHint() {
        var r = selectedR
        var c = selectedC
        if (r < 0 || puzzle[r][c] == solved[r][c]) {
            outer@ for (i in 0 until size) {
                for (j in 0 until size) {
                    if (puzzle[i][j] != solved[i][j]) { r = i; c = j; break@outer }
                }
            }
        }
        if (r < 0) return
        puzzle[r][c] = solved[r][c]
        fixed[r][c] = true
        selectedR = r
        selectedC = c
        refreshBoard()
        checkWin()
    }

    private fun refreshBoard() {
        board.selectedR = selectedR
        board.selectedC = selectedC
        board.invalidate()
    }

    private fun checkWin() {
        for (r in 0 until size)
            for (c in 0 until size)
                if (puzzle[r][c] != solved[r][c]) return

        running = false
        if (errors == 0) {
            Prefs.saveRecord(this, size, level, seconds)
        }
        Toast.makeText(
            this,
            "Победа! Время: ${Prefs.formatTime(seconds)}, ошибок: $errors",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("size", size)
        outState.putInt("level", level)
        outState.putInt("seconds", seconds)
        outState.putInt("errors", errors)
        outState.putInt("selR", selectedR)
        outState.putInt("selC", selectedC)

        val flatPuzzle = ArrayList<Int>()
        val flatSolved = ArrayList<Int>()
        for (r in 0 until size) for (c in 0 until size) {
            flatPuzzle.add(puzzle[r][c])
            flatSolved.add(solved[r][c])
        }
        outState.putIntegerArrayList("puzzle", flatPuzzle)
        outState.putIntegerArrayList("solved", flatSolved)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        size = savedInstanceState.getInt("size", Prefs.getSize(this))
        level = savedInstanceState.getInt("level", Prefs.getLevel(this))
        boxRows = 2
        boxCols = if (size == 4) 2 else 3

        val flatPuzzle = savedInstanceState.getIntegerArrayList("puzzle")!!
        val flatSolved = savedInstanceState.getIntegerArrayList("solved")!!

        puzzle = Array(size) { IntArray(size) }
        solved = Array(size) { IntArray(size) }
        var k = 0
        for (r in 0 until size) for (c in 0 until size) {
            puzzle[r][c] = flatPuzzle[k]
            solved[r][c] = flatSolved[k]
            k++
        }

        fixed = Array(size) { r -> BooleanArray(size) { c -> puzzle[r][c] != 0 } }

        seconds = savedInstanceState.getInt("seconds")
        errors = savedInstanceState.getInt("errors")
        selectedR = savedInstanceState.getInt("selR")
        selectedC = savedInstanceState.getInt("selC")
        running = true

        tvTimer.text = "Время: ${Prefs.formatTime(seconds)}"
        tvErrors.text = "Ошибки: $errors"

        board.setBoard(size, boxRows, boxCols, puzzle, fixed)
        board.selectedR = selectedR
        board.selectedC = selectedC
        board.invalidate()

        buildNumberButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tick)
    }
}