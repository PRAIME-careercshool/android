package com.example.hackathon

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class VoiceDiaryConvertingActivity : AppCompatActivity() {
    
    private lateinit var loadingIcon: ImageView
    private var animationHandler: Handler? = null
    private var animationRunnable: Runnable? = null
    private var moveToCompleteHandler: Handler? = null
    private var moveToCompleteRunnable: Runnable? = null
    private var currentImageIndex = 0
    private var isDestroyed = false
    
    private val loadingImages = listOf(
        R.drawable.record_loading_1,
        R.drawable.record_loading_2,
        R.drawable.record_loading_3
    )
    
    private val scaleValues = listOf(
        1.0f, // record_loading_1 - 기본 크기
        1.3f, // record_loading_2 - 파동 확산
        1.6f  // record_loading_3 - 파동 최대 확산
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_converting)
        
        setupViews()
        startLoadingAnimation()
        scheduleMoveToComplete()
    }
    
    private fun setupViews() {
        loadingIcon = findViewById(R.id.loadingIcon)
        if (loadingIcon == null) {
            // 레이아웃에서 loadingIcon을 찾을 수 없는 경우 - 로그만 출력하고 계속 진행
            android.util.Log.e("VoiceDiaryConverting", "loadingIcon을 찾을 수 없습니다")
            return
        }
    }
    
    private fun scheduleMoveToComplete() {
        moveToCompleteHandler = Handler(Looper.getMainLooper())
        moveToCompleteRunnable = Runnable {
            if (!isDestroyed) {
                moveToCompletePage()
            }
        }
        moveToCompleteHandler?.postDelayed(moveToCompleteRunnable!!, 3000)
    }
    
    private fun startLoadingAnimation() {
        if (isDestroyed) return
        
        // loadingIcon이 초기화되지 않은 경우 애니메이션을 시작하지 않음
        if (!::loadingIcon.isInitialized) {
            android.util.Log.e("VoiceDiaryConverting", "loadingIcon이 초기화되지 않았습니다")
            return
        }
        
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                if (!isDestroyed && ::loadingIcon.isInitialized) {
                    animateToNextImage()
                    currentImageIndex = (currentImageIndex + 1) % loadingImages.size
                    animationHandler?.postDelayed(this, 500)
                }
            }
        }
        animationHandler?.post(animationRunnable!!)
    }
    
    private fun animateToNextImage() {
        if (isDestroyed || !::loadingIcon.isInitialized) return
        
        try {
            val nextImageIndex = (currentImageIndex + 1) % loadingImages.size
            val currentScale = scaleValues[currentImageIndex]
            val nextScale = scaleValues[nextImageIndex]
            
            // 이미지 변경
            loadingIcon.setImageResource(loadingImages[nextImageIndex])
            
            // Scale 애니메이션
            val scaleXAnimator = ObjectAnimator.ofFloat(loadingIcon, "scaleX", currentScale, nextScale)
            val scaleYAnimator = ObjectAnimator.ofFloat(loadingIcon, "scaleY", currentScale, nextScale)
            
            scaleXAnimator.setDuration(500)
            scaleYAnimator.setDuration(500)
            
            scaleXAnimator.start()
            scaleYAnimator.start()
        } catch (e: Exception) {
            // 애니메이션 중 오류 발생 시 로그만 출력하고 계속 진행
            e.printStackTrace()
        }
    }
    
    private fun stopLoadingAnimation() {
        animationHandler?.removeCallbacks(animationRunnable!!)
        animationHandler = null
        animationRunnable = null
    }
    
    private fun stopMoveToComplete() {
        moveToCompleteHandler?.removeCallbacks(moveToCompleteRunnable!!)
        moveToCompleteHandler = null
        moveToCompleteRunnable = null
    }
    
    private fun moveToCompletePage() {
        android.util.Log.d("VoiceDiaryConverting", "moveToCompletePage() 호출됨")
        if (isDestroyed) {
            android.util.Log.w("VoiceDiaryConverting", "Activity가 이미 종료됨")
            return
        }
        
        try {
            android.util.Log.d("VoiceDiaryConverting", "Complete Activity로 전환 시도")
            stopLoadingAnimation()
            stopMoveToComplete()
            
            val intent = Intent(this, VoiceDiaryCompleteActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            android.util.Log.d("VoiceDiaryConverting", "Complete Activity 전환 성공")
            
            // finish() 호출을 지연시켜 Activity 전환이 완료된 후 종료
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isDestroyed) {
                    android.util.Log.d("VoiceDiaryConverting", "현재 Activity 종료")
                    finish()
                }
            }, 100)
        } catch (e: Exception) {
            android.util.Log.e("VoiceDiaryConverting", "Activity 전환 실패", e)
            // 예외 발생 시에도 Activity 전환 시도
            try {
                android.util.Log.d("VoiceDiaryConverting", "예외 발생 후 재시도")
                val intent = Intent(this, VoiceDiaryCompleteActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                android.util.Log.d("VoiceDiaryConverting", "재시도 성공")
                
                // 재시도 성공 시에도 지연된 finish() 호출
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isDestroyed) {
                        android.util.Log.d("VoiceDiaryConverting", "재시도 후 현재 Activity 종료")
                        finish()
                    }
                }, 100)
            } catch (e2: Exception) {
                android.util.Log.e("VoiceDiaryConverting", "재시도도 실패", e2)
                // 최후의 수단으로도 Activity 전환이 실패한 경우
                android.util.Log.e("VoiceDiaryConverting", "모든 Activity 전환 시도 실패")
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isDestroyed = true
        stopLoadingAnimation()
        stopMoveToComplete()
    }
    
    override fun onBackPressed() {
        // 뒤로가기 비활성화 (변환 중이므로)
        // super.onBackPressed()
    }
}