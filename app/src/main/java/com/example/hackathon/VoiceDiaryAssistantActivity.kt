package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VoiceDiaryAssistantActivity : AppCompatActivity() {
    
    private var isPaused = false
    private lateinit var pauseButton: ImageView
    private lateinit var endButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_assistant)
        
        setupButtonListeners()
        
        // 실제 구현에서는 사용자가 "시작" 버튼을 눌렀을 때 이동하도록 구현
        // 여기서는 데모를 위해 자동으로 이동
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, VoiceDiaryCallActivity::class.java)
            startActivity(intent)
        }, 3000)
    }
    
    private fun setupButtonListeners() {
        // 일시정지/재생 버튼
        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener {
            togglePause()
        }
        
        // 종료 버튼
        endButton = findViewById(R.id.endButton)
        endButton.setOnClickListener {
            endCall()
        }
    }
    
    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            // 일시정지 상태 - record_continue 이미지로 변경
            pauseButton.setImageResource(R.drawable.record_continue)
            Toast.makeText(this, "일시정지되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            // 재생 상태 - record_pause 이미지로 변경
            pauseButton.setImageResource(R.drawable.record_pause)
            Toast.makeText(this, "재생되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun endCall() {
        Toast.makeText(this, "통화가 종료되었습니다", Toast.LENGTH_SHORT).show()
        // 메인 페이지로 돌아가기
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
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
