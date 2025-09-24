package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VoiceDiaryCallActivity : AppCompatActivity() {
    
    private var isPaused = false
    private lateinit var pauseButton: ImageView
    private lateinit var endButton: ImageView
    private lateinit var recordingIcon: ImageView
    
    private var animationHandler: Handler? = null
    private var animationRunnable: Runnable? = null
    private var currentImageIndex = 0
    
    private val loadingImages = listOf(
        R.drawable.record_loading_1,
        R.drawable.record_loading_2,
        R.drawable.record_loading_3
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_call)
        
        setupButtonListeners()
        startRecordingAnimation()
    }
    
    private fun setupButtonListeners() {
        // 녹음 아이콘
        recordingIcon = findViewById(R.id.recordingIcon)
        
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
            stopRecordingAnimation()
            Toast.makeText(this, "일시정지되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            // 재생 상태 - record_pause 이미지로 변경
            pauseButton.setImageResource(R.drawable.record_pause)
            startRecordingAnimation()
            Toast.makeText(this, "재생되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startRecordingAnimation() {
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                recordingIcon.setImageResource(loadingImages[currentImageIndex])
                currentImageIndex = (currentImageIndex + 1) % loadingImages.size
                animationHandler?.postDelayed(this, 500) // 0.5초마다 이미지 변경
            }
        }
        animationHandler?.post(animationRunnable!!)
    }
    
    private fun stopRecordingAnimation() {
        animationHandler?.removeCallbacks(animationRunnable!!)
        animationHandler = null
        animationRunnable = null
    }
    
    private fun endCall() {
        stopRecordingAnimation()
        Toast.makeText(this, "통화가 종료되었습니다", Toast.LENGTH_SHORT).show()
        // 메인 페이지로 돌아가기
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopRecordingAnimation()
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
