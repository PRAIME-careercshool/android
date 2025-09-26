package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class VoiceDiaryAssistantActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_assistant)
        
        setupButtonListeners()
    }
    
    private fun setupButtonListeners() {
        // 중앙 마이크 아이콘 클릭 시 통화 시작
        findViewById<androidx.cardview.widget.CardView>(R.id.micCardView)?.setOnClickListener {
            startCall()
        }
    }
    
    private fun startCall() {
        val intent = Intent(this, VoiceDiaryCallActivity::class.java)
        startActivity(intent)
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
