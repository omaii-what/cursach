package com.example.cursach

import kotlin.random.Random

object Sudoku {

    // Генерирует решённое судоку нужного размера (4 или 6)
    fun generateSolved(size: Int): Array<IntArray> {
        val boxRows = if (size == 4) 2 else 2
        val boxCols = if (size == 4) 2 else 3
        val grid = Array(size) { IntArray(size) }
        fill(grid, size, boxRows, boxCols)
        return grid
    }

    // Рекурсивное заполнение (простой backtracking)
    private fun fill(grid: Array<IntArray>, size: Int, boxRows: Int, boxCols: Int): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == 0) {
                    val nums = (1..size).shuffled()
                    for (n in nums) {
                        if (isValid(grid, r, c, n, size, boxRows, boxCols)) {
                            grid[r][c] = n
                            if (fill(grid, size, boxRows, boxCols)) return true
                            grid[r][c] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Проверка: можно ли поставить число n в клетку (r, c)
    fun isValid(
        grid: Array<IntArray>, r: Int, c: Int, n: Int,
        size: Int, boxRows: Int, boxCols: Int
    ): Boolean {
        for (i in 0 until size) {
            if (grid[r][i] == n) return false
            if (grid[i][c] == n) return false
        }
        val startR = (r / boxRows) * boxRows
        val startC = (c / boxCols) * boxCols
        for (i in startR until startR + boxRows) {
            for (j in startC until startC + boxCols) {
                if (grid[i][j] == n) return false
            }
        }
        return true
    }

    // Удаляет часть клеток — чем выше сложность, тем больше пустых
    fun makePuzzle(solved: Array<IntArray>, size: Int, emptyCount: Int): Array<IntArray> {
        val puzzle = Array(size) { solved[it].clone() }
        var removed = 0
        while (removed < emptyCount) {
            val r = Random.nextInt(size)
            val c = Random.nextInt(size)
            if (puzzle[r][c] != 0) {
                puzzle[r][c] = 0
                removed++
            }
        }
        return puzzle
    }
}