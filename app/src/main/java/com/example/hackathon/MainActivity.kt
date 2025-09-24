package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupButtonListeners()
        setDate()
    }

    private fun setDate() {
        val dateTextView = findViewById<TextView>(R.id.dateText)
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        val currentDate = sdf.format(Date())
        dateTextView.text = currentDate
    }
    
    private fun setupButtonListeners() {

        
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
        
        // 일지 작성 카드
        findViewById<androidx.cardview.widget.CardView>(R.id.btnDiaryCreate).setOnClickListener {
            val intent = Intent(this, DiaryCreateActivity::class.java)
            startActivity(intent)
        }
        
        // 전화 버튼 (음성 일지 작성)
        findViewById<android.widget.LinearLayout>(R.id.btnPhone).setOnClickListener {
            val intent = Intent(this, VoiceDiaryAssistantActivity::class.java)
            startActivity(intent)
        }

        // 내 정보 버튼
        findViewById<android.widget.LinearLayout>(R.id.btnMyInfo).setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }
    }
}