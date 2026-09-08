package com.example.myapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val btnClick = findViewById<Button>(R.id.btnClick)

        btnClick.setOnClickListener {
            tvTitle.text = "Congratulations! It worked!"
            Toast.makeText(this, "Success! Generated on Phone", Toast.LENGTH_SHORT).show()
        }
    }
}
