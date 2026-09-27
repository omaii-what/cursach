package com.example.cursach

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GridSize : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grids)

        findViewById<Button>(R.id.btnFour).setOnClickListener {
            Prefs.setSize(this, 4)
            finish()
        }
        findViewById<Button>(R.id.btnSix).setOnClickListener {
            Prefs.setSize(this, 6)
            finish()
        }
        findViewById<Button>(R.id.backOk).setOnClickListener {
            finish()
        }
    }
}