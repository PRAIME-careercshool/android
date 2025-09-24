package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class VoiceDiaryCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_complete)
        
        setupButtonListeners()
    }
    
    private fun setupButtonListeners() {
        // 내용 확인 버튼
        findViewById<CardView>(R.id.btnConfirmContent).setOnClickListener {
            val intent = Intent(this, DiaryListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로가기 버튼을 눌렀을 때 메인페이지로 이동
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
