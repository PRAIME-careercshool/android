package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout

class VoiceDiaryCompleteActivity : AppCompatActivity() {
    
    private lateinit var btnConfirmContent: LinearLayout
    private var isDestroyed = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_complete)
        
        setupViews()
        setupButtonListeners()
    }
    
    private fun setupViews() {
        btnConfirmContent = findViewById(R.id.btnConfirmContent)
        if (btnConfirmContent == null) {
            // 레이아웃에서 버튼을 찾을 수 없는 경우 - 로그만 출력하고 계속 진행
            android.util.Log.e("VoiceDiaryComplete", "btnConfirmContent를 찾을 수 없습니다")
            return
        }
    }
    
    private fun setupButtonListeners() {
        if (::btnConfirmContent.isInitialized && btnConfirmContent != null) {
            btnConfirmContent.setOnClickListener {
                if (!isDestroyed) {
                    // 일지 목록 페이지로 이동
                    moveToDiaryList()
                }
            }
        } else {
            android.util.Log.e("VoiceDiaryComplete", "btnConfirmContent가 초기화되지 않았습니다")
        }
    }
    
    private fun moveToDiaryList() {
        if (isDestroyed) return
        
        try {
            // 일지 목록 페이지로 이동 (MainActivity 또는 DiaryListActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // 액티비티 전환 중 오류 발생 시 로그 출력
            e.printStackTrace()
            finish()
        }
    }
    
    override fun onBackPressed() {
        if (!isDestroyed) {
            // 뒤로가기 시 메인 페이지로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isDestroyed = true
    }
}