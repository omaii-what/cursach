package com.example.cursach

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class LevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        val radio = findViewById<RadioGroup>(R.id.radioLevel)
        val rEasy = findViewById<RadioButton>(R.id.radioEasy)
        val rMedium = findViewById<RadioButton>(R.id.radioMedium)
        val rHard = findViewById<RadioButton>(R.id.radioHard)

        when (Prefs.getLevel(this)) {
            0 -> rEasy.isChecked = true
            1 -> rMedium.isChecked = true
            else -> rHard.isChecked = true
        }

        findViewById<Button>(R.id.btnOk).setOnClickListener {
            val level = when (radio.checkedRadioButtonId) {
                R.id.radioEasy -> 0
                R.id.radioMedium -> 1
                else -> 2
            }
            Prefs.setLevel(this, level)
            finish()
        }
    }
}