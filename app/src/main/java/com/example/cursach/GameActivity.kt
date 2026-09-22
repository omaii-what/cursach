package com.example.cursach

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    private lateinit var gridView: GridView
    private lateinit var adapter: SudokuAdapter
    private lateinit var tvTimer: TextView
    private lateinit var tvErrors: TextView

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

        size = Prefs.getSize(this)
        level = Prefs.getLevel(this)
        boxCols = if (size == 4) 2 else 3
        boxRows = 2

        solved = Sudoku.generateSolved(size)

        val total = size * size
        val emptyCount = when (level) {
            0 -> total / 3
            1 -> total / 2
            else -> total - 2
        }
        puzzle = Sudoku.makePuzzle(solved, size, emptyCount)
        fixed = Array(size) { r -> BooleanArray(size) { c -> puzzle[r][c] != 0 } }

        tvTimer = findViewById(R.id.tvTimer)
        tvErrors = findViewById(R.id.tvErrors)
        tvTimer.text = "Время: 00:00"

        gridView = findViewById(R.id.grid)
        gridView.numColumns = size

        adapter = SudokuAdapter()
        gridView.adapter = adapter
        gridView.setOnItemClickListener { _, _, pos, _ ->
            selectedR = pos / size
            selectedC = pos % size
            adapter.notifyDataSetChanged()
        }

        buildNumberButtons()
        findViewById<Button>(R.id.btnHint).setOnClickListener { showHint() }

        handler.postDelayed(tick, 1000)
    }

    private fun buildNumberButtons() {
        val row = findViewById<LinearLayout>(R.id.numbersRow)
        row.removeAllViews()
        for (n in 1..size) {
            val btn = Button(this)
            btn.text = n.toString()
            btn.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
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
        adapter.notifyDataSetChanged()
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
        adapter.notifyDataSetChanged()
        checkWin()
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
        outState.putInt("seconds", seconds)
        outState.putInt("errors", errors)
        outState.putInt("selR", selectedR)
        outState.putInt("selC", selectedC)
        val flat = ArrayList<Int>()
        for (r in 0 until size) for (c in 0 until size) flat.add(puzzle[r][c])
        outState.putIntegerArrayList("puzzle", flat)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        seconds = savedInstanceState.getInt("seconds")
        errors = savedInstanceState.getInt("errors")
        selectedR = savedInstanceState.getInt("selR")
        selectedC = savedInstanceState.getInt("selC")
        val flat = savedInstanceState.getIntegerArrayList("puzzle")!!
        var k = 0
        for (r in 0 until size) for (c in 0 until size) puzzle[r][c] = flat[k++]
        tvTimer.text = "Время: ${Prefs.formatTime(seconds)}"
        tvErrors.text = "Ошибки: $errors"
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tick)
    }

    inner class SudokuAdapter : BaseAdapter() {
        override fun getCount(): Int = size * size
        override fun getItem(position: Int): Any = position
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val tv = convertView as? TextView ?: TextView(this@GameActivity).apply {
                gravity = Gravity.CENTER
                textSize = 22f
                layoutParams = AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val r = position / size
            val c = position % size
            val value = puzzle[r][c]

            tv.text = if (value == 0) "" else value.toString()
            tv.setTextColor(if (fixed[r][c]) Color.BLACK else Color.rgb(0, 0, 200))
            tv.setBackgroundColor(
                if (r == selectedR && c == selectedC) Color.LTGRAY else Color.TRANSPARENT
            )

            val left = if (c % boxCols == 0) 3 else 1
            val top = if (r % boxRows == 0) 3 else 1
            val right = if (c == size - 1) 3 else 1
            val bottom = if (r == size - 1) 3 else 1
            tv.setPadding(8 + left, 24 + top, 8 + right, 24 + bottom)

            return tv
        }
    }
}