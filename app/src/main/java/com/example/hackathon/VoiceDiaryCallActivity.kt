package com.example.hackathon

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VoiceDiaryCallActivity : AppCompatActivity() {
    
    private var isPaused = false
    private lateinit var pauseButton: ImageView
    private lateinit var endButton: ImageView
    private lateinit var recordingIcon: ImageView
    private lateinit var timeTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var progressText: TextView
    
    private var animationHandler: Handler? = null
    private var animationRunnable: Runnable? = null
    private var currentImageIndex = 0
    private var currentProgress = 1 // 현재 진행률 (1-7)
    
    // 통화 시간 관련 변수들
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var totalPausedTime: Long = 0
    
    private val loadingImages = listOf(
        R.drawable.record_loading_1,
        R.drawable.record_loading_2,
        R.drawable.record_loading_3
    )
    
    private val scaleValues = listOf(
        1.0f, // record_loading_1 - 기본 크기
        1.3f, // record_loading_2 - 파동 확산 (50dp 증가)
        1.6f  // record_loading_3 - 파동 최대 확산 (100dp 증가)
    )
    
    private val questions = listOf(
        "안녕하세요! 농담 AI 어시스턴트 '농지기'입니다. 오늘 하루 농사일 어떠셨나요?",
        "재배지가 어디인지 알려주세요.",
        "작물은 어떤 것을 재배하고 계신가요?",
        "오늘 어떤 작업을 하셨나요?",
        "농약은 어떤 것을 사용하셨나요?",
        "비료는 어떤 것을 사용하셨나요?",
        "특이한 점이나 주의사항이 있으신가요?"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_call)
        
        setupButtonListeners()
        startRecordingAnimation()
        startCallTimer()
    }
    
    private fun setupButtonListeners() {
        // 녹음 아이콘
        recordingIcon = findViewById(R.id.recordingIcon)
        
        // 시간 표시 TextView
        timeTextView = findViewById(R.id.timeTextView)
        
        // 질문 TextView
        questionTextView = findViewById(R.id.questionTextView)
        
        // 진행률 관련 UI
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        
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
        
        // 초기 질문 설정
        updateQuestion()
    }
    
    private fun updateQuestion() {
        if (currentProgress <= questions.size) {
            questionTextView.text = questions[currentProgress - 1]
            updateProgress()
        }
    }
    
    private fun updateProgress() {
        val progressPercentage = (currentProgress * 100) / 7
        progressBar.progress = progressPercentage
        progressText.text = "$currentProgress/7"
    }
    
    private fun nextQuestion() {
        if (currentProgress < 7) {
            currentProgress++
            updateQuestion()
        } else {
            // 7/7 완료 시 변환 페이지로 이동
            moveToConvertingPage()
        }
    }
    
    private fun moveToConvertingPage() {
        stopRecordingAnimation()
        stopCallTimer()
        
        // 변환 페이지로 이동
        val intent = Intent(this, VoiceDiaryConvertingActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            // 일시정지 상태 - record_continue 이미지로 변경
            pauseButton.setImageResource(R.drawable.record_continue)
            stopRecordingAnimation()
            pauseCallTimer()
            Toast.makeText(this, "일시정지되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            // 재생 상태 - record_pause 이미지로 변경
            pauseButton.setImageResource(R.drawable.record_pause)
            startRecordingAnimation()
            resumeCallTimer()
            Toast.makeText(this, "재생되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startRecordingAnimation() {
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                animateToNextImage()
                currentImageIndex = (currentImageIndex + 1) % loadingImages.size
                
                // 3초마다 다음 질문으로 진행 (6번의 이미지 변경 = 3초)
                if (currentImageIndex == 0) {
                    nextQuestion()
                }
                
                animationHandler?.postDelayed(this, 500) // 0.5초마다 이미지 변경 속도 (더 빠르게)
            }
        }
        animationHandler?.post(animationRunnable!!)
    }
    
    private fun animateToNextImage() {
        val nextImageIndex = (currentImageIndex + 1) % loadingImages.size
        val currentScale = scaleValues[currentImageIndex]
        val nextScale = scaleValues[nextImageIndex]
        
        // 이미지 변경
        recordingIcon.setImageResource(loadingImages[nextImageIndex])
        
        // Scale 애니메이션 (레이아웃에 영향 없음)
        val scaleXAnimator = ObjectAnimator.ofFloat(recordingIcon, "scaleX", currentScale, nextScale)
        val scaleYAnimator = ObjectAnimator.ofFloat(recordingIcon, "scaleY", currentScale, nextScale)
        
        scaleXAnimator.setDuration(500) // 0.5초 동안 부드럽게 변환
        scaleYAnimator.setDuration(500)
        
        scaleXAnimator.start()
        scaleYAnimator.start()
    }
    
    private fun stopRecordingAnimation() {
        animationHandler?.removeCallbacks(animationRunnable!!)
        animationHandler = null
        animationRunnable = null
    }
    
    private fun startCallTimer() {
        startTime = System.currentTimeMillis()
        timerHandler = Handler(Looper.getMainLooper())
        timerRunnable = object : Runnable {
            override fun run() {
                updateCallTime()
                timerHandler?.postDelayed(this, 1000) // 1초마다 업데이트
            }
        }
        timerHandler?.post(timerRunnable!!)
    }
    
    private fun pauseCallTimer() {
        pausedTime = System.currentTimeMillis()
        timerHandler?.removeCallbacks(timerRunnable!!)
    }
    
    private fun resumeCallTimer() {
        if (pausedTime > 0) {
            totalPausedTime += System.currentTimeMillis() - pausedTime
            pausedTime = 0
        }
        timerHandler?.post(timerRunnable!!)
    }
    
    private fun stopCallTimer() {
        timerHandler?.removeCallbacks(timerRunnable!!)
        timerHandler = null
        timerRunnable = null
    }
    
    private fun updateCallTime() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime - totalPausedTime
        
        val seconds = (elapsedTime / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        
        val timeString = String.format("%02d:%02d", minutes, remainingSeconds)
        timeTextView.text = timeString
    }
    
    private fun endCall() {
        stopRecordingAnimation()
        stopCallTimer()
        Toast.makeText(this, "통화가 종료되었습니다", Toast.LENGTH_SHORT).show()
        // 무조건 converting 페이지로 이동
        moveToConvertingPage()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopRecordingAnimation()
        stopCallTimer()
    }
    
    override fun onBackPressed() {
        // 뒤로가기 비활성화 (통화 중이므로 무조건 converting으로만 이동)
        // super.onBackPressed()
    }
}
