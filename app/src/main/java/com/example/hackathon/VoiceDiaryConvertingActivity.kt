package com.example.hackathon

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
    private var currentImageIndex = 0
    
    private val loadingImages = listOf(
        R.drawable.record_loading_1,
        R.drawable.record_loading_2,
        R.drawable.record_loading_3
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary_converting)
        
        loadingIcon = findViewById(R.id.loadingIcon)
        startLoadingAnimation()
    }
    
    private fun startLoadingAnimation() {
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                loadingIcon.setImageResource(loadingImages[currentImageIndex])
                currentImageIndex = (currentImageIndex + 1) % loadingImages.size
                animationHandler?.postDelayed(this, 500) // 0.5초마다 이미지 변경
            }
        }
        animationHandler?.post(animationRunnable!!)
    }
    
    private fun stopLoadingAnimation() {
        animationHandler?.removeCallbacks(animationRunnable!!)
        animationHandler = null
        animationRunnable = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopLoadingAnimation()
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
