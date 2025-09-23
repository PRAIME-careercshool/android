package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_list)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 뒤로가기 버튼
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}

