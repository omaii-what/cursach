package com.example.sudoku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val radioSize = findViewById<RadioGroup>(R.id.radioSize)
        val radioDiff = findViewById<RadioGroup>(R.id.radioDiff)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val tvRecords = findViewById<TextView>(R.id.tvRecords)

        showRecords(tvRecords)

        btnStart.setOnClickListener {
            val size = if (radioSize.checkedRadioButtonId == R.id.radio6) 6 else 4
            val hard = radioDiff.checkedRadioButtonId == R.id.radioHard

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("size", size)
            intent.putExtra("hard", hard)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showRecords(findViewById(R.id.tvRecords))
    }

    private fun showRecords(tv: TextView) {
        val prefs = getSharedPreferences("sudoku", MODE_PRIVATE)
        val r4 = prefs.getInt("record_4", 0)
        val r6 = prefs.getInt("record_6", 0)
        tv.text = "4×4: $r4 сек\n6×6: $r6 сек"
    }
}