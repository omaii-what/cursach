package com.example.cursach

import android.content.Context

object Prefs {
    private const val NAME = "sudoku"

    // Настройки
    fun getLevel(ctx: Context): Int = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        .getInt("level", 0) // 0 - легко, 1 - средне, 2 - сложно

    fun setLevel(ctx: Context, level: Int) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putInt("level", level).apply()
    }

    fun getSize(ctx: Context): Int = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        .getInt("size", 4)

    fun setSize(ctx: Context, size: Int) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putInt("size", size).apply()
    }

    // Рекорды: ключ "record_{size}_{level}" -> секунды
    fun getRecord(ctx: Context, size: Int, level: Int): Int =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getInt("record_${size}_$level", 0)

    fun saveRecord(ctx: Context, size: Int, level: Int, seconds: Int) {
        val prefs = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val key = "record_${size}_$level"
        val old = prefs.getInt(key, 0)
        if (old == 0 || seconds < old) {
            prefs.edit().putInt(key, seconds).apply()
        }
    }

    fun formatTime(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "%02d:%02d".format(m, s)
    }

    fun levelName(level: Int) = when (level) {
        0 -> "Лёгкий"
        1 -> "Средний"
        else -> "Сложный"
    }
}