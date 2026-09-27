package com.example.cursach

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        findViewById<Button>(R.id.btnEasy).setOnClickListener {
            Prefs.setLevel(this, 0)
            finish()
        }
        findViewById<Button>(R.id.btnMed).setOnClickListener {
            Prefs.setLevel(this, 1)
            finish()
        }
        findViewById<Button>(R.id.btnHard).setOnClickListener {
            Prefs.setLevel(this, 2)
            finish()
        }
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}