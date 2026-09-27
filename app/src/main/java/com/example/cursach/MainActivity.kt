package com.example.cursach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnGrid: Button
    private lateinit var btnDifficulty: Button
    private lateinit var tvRecords: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGrid = findViewById(R.id.btnGrid)
        btnDifficulty = findViewById(R.id.btnDifficulty)
        tvRecords = findViewById(R.id.tvRecords)

        btnGrid.setOnClickListener {
            startActivity(Intent(this, GridSize::class.java))
        }
        btnDifficulty.setOnClickListener {
            startActivity(Intent(this, LevelActivity::class.java))
        }
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun updateUi() {
        val level = Prefs.getLevel(this)
        val size = Prefs.getSize(this)

        btnDifficulty.text = "Изменить сложность\n(${Prefs.levelName(level)})"
        btnGrid.text = "Изменить размер сетки\n(${size}×${size})"

        val sb = StringBuilder()
        for (s in intArrayOf(4, 6)) {
            for (l in 0..2) {
                val rec = Prefs.getRecord(this, s, l)
                val time = if (rec == 0) "—" else Prefs.formatTime(rec)
                sb.append("${s}×${s}  •  ${Prefs.levelName(l)}: $time\n")
            }
        }
        tvRecords.text = sb.toString().trimEnd()
    }
}