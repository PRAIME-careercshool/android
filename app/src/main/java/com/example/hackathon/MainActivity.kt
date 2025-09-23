package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupButtonListeners()
    }
    
    private fun setupButtonListeners() {
        // 일지 작성 카드
        findViewById<androidx.cardview.widget.CardView>(R.id.btnDiaryCreate).setOnClickListener {
            // TODO: 일지 작성 화면으로 이동
        }
        
        // 장부 목록 카드
        findViewById<androidx.cardview.widget.CardView>(R.id.btnAccountList).setOnClickListener {
            val intent = Intent(this, AccountListActivity::class.java)
            startActivity(intent)
        }
        
        // 장부 작성 카드
        findViewById<androidx.cardview.widget.CardView>(R.id.btnAccountCreate).setOnClickListener {
            val intent = Intent(this, AccountCreateActivity::class.java)
            startActivity(intent)
        }
        
        // 일지 목록 카드
        findViewById<androidx.cardview.widget.CardView>(R.id.btnDiaryList).setOnClickListener {
            val intent = Intent(this, DiaryListActivity::class.java)
            startActivity(intent)
        }
    }
}