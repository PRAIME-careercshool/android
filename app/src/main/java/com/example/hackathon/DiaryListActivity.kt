package com.example.hackathon

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DiaryListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 뒤로가기 버튼
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}

