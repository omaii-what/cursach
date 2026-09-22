package com.example.cursach

class GridSizepackage com.example.cursach

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class SizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_size)

        val radio = findViewById<RadioGroup>(R.id.radioSize)
        val r4 = findViewById<RadioButton>(R.id.radio4)
        val r6 = findViewById<RadioButton>(R.id.radio6)

        if (Prefs.getSize(this) == 6) r6.isChecked = true else r4.isChecked = true

        findViewById<Button>(R.id.btnOk).setOnClickListener {
            val size = if (radio.checkedRadioButtonId == R.id.radio6) 6 else 4
            Prefs.setSize(this, size)
            finish()
        }
    }
} {
}