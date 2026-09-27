package com.example.cursach

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SudokuBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var size = 4
        set(v) { field = v; invalidate() }

    var boxRows = 2
    var boxCols = 2

    var puzzle: Array<IntArray>? = null
        set(v) { field = v; invalidate() }

    var fixed: Array<BooleanArray>? = null
        set(v) { field = v; invalidate() }
    var selectedR = -1
    var selectedC = -1
    var onCellSelected: ((Int, Int) -> Unit)? = null

    fun setBoard(
        newSize: Int,
        newBoxRows: Int,
        newBoxCols: Int,
        newPuzzle: Array<IntArray>,
        newFixed: Array<BooleanArray>
    ) {
        size = newSize
        boxRows = newBoxRows
        boxCols = newBoxCols
        puzzle = newPuzzle
        fixed = newFixed
        selectedR = -1
        selectedC = -1
        invalidate()
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(50, 90, 175)
        style = Paint.Style.STROKE
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val p = puzzle ?: return
        val f = fixed ?: return

        val side = minOf(width, height)
        val offsetX = (width - side) / 2f
        val offsetY = (height - side) / 2f

        val cellW = side.toFloat() / size
        val cellH = side.toFloat() / size

        textPaint.textSize = minOf(cellW, cellH) * 0.6f

        for (r in 0 until size) {
            for (c in 0 until size) {
                val left = offsetX + c * cellW
                val top = offsetY + r * cellH
                val right = left + cellW
                val bottom = top + cellH

                bgPaint.color = if (r == selectedR && c == selectedC)
                    Color.LTGRAY else Color.WHITE
                canvas.drawRect(left, top, right, bottom, bgPaint)

                val v = p[r][c]
                if (v != 0) {
                    textPaint.color = if (f[r][c]) Color.BLACK else Color.rgb(0, 0, 200)
                    val x = left + cellW / 2f
                    val y = top + cellH / 2f -
                            (textPaint.descent() + textPaint.ascent()) / 2f
                    canvas.drawText(v.toString(), x, y, textPaint)
                }
            }
        }

        val thin = 1f * resources.displayMetrics.density
        val thick = 4f * resources.displayMetrics.density

        for (i in 0..size) {
            val isBorder = (i % boxRows == 0) || (i == size)
            linePaint.strokeWidth = if (isBorder) thick else thin
            val y = offsetY + i * cellH
            canvas.drawLine(offsetX, y, offsetX + side, y, linePaint)
        }
        for (j in 0..size) {
            val isBorder = (j % boxCols == 0) || (j == size)
            linePaint.strokeWidth = if (isBorder) thick else thin
            val x = offsetX + j * cellW
            canvas.drawLine(x, offsetY, x, offsetY + side, linePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return true
        if (puzzle == null) return true

        val side = minOf(width, height)
        val offsetX = (width - side) / 2f
        val offsetY = (height - side) / 2f

        val cellW = side.toFloat() / size
        val cellH = side.toFloat() / size

        val relX = event.x - offsetX
        val relY = event.y - offsetY

        if (relX < 0 || relY < 0 || relX >= side || relY >= side) return true

        val c = (relX / cellW).toInt().coerceIn(0, size - 1)
        val r = (relY / cellH).toInt().coerceIn(0, size - 1)

        selectedR = r
        selectedC = c
        onCellSelected?.invoke(r, c)
        invalidate()
        return true
    }
}