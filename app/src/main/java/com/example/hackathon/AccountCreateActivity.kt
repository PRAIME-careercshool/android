package com.example.hackathon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AccountCreateActivity : AppCompatActivity() {
    
    private lateinit var photoURI: Uri
    private val CAMERA_PERMISSION_CODE = 100
    
    // 카메라 결과를 처리하는 ActivityResultLauncher
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(this, "영수증이 첨부되었습니다!", Toast.LENGTH_SHORT).show()
            // 촬영한 이미지를 ImageView에 표시
            displayCapturedImage()
        } else {
            Toast.makeText(this, "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_create)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 뒤로가기 버튼
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        // 저장 버튼
        findViewById<CardView>(R.id.btnSave).setOnClickListener {
            // TODO: 저장 로직 구현
            finish()
        }
        
        // 취소 버튼
        findViewById<CardView>(R.id.btnCancel).setOnClickListener {
            finish()
        }
        
        // 금액 버튼들
        findViewById<CardView>(R.id.btn10000).setOnClickListener {
            // TODO: 1만원 입력
        }
        
        findViewById<CardView>(R.id.btn50000).setOnClickListener {
            // TODO: 5만원 입력
        }
        
        findViewById<CardView>(R.id.btn100000).setOnClickListener {
            // TODO: 10만원 입력
        }
        

        
        findViewById<CardView>(R.id.btn1000000).setOnClickListener {
            // TODO: 100만원 입력
        }
        
        // 사진 업로드
        findViewById<CardView>(R.id.photoUploadArea).setOnClickListener {
            checkCameraPermissionAndTakePhoto()
        }
    }
    
    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 있으면 카메라 실행
                openCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                // 권한 설명이 필요한 경우
                Toast.makeText(this, "영수증 촬영을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
            else -> {
                // 권한 요청
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }
    }
    
    private fun openCamera() {
        val photoFile = createImageFile()
        photoFile?.let {
            photoURI = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            cameraLauncher.launch(photoURI)
        }
    }
    
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "RECEIPT_${timeStamp}_"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: Exception) {
            Toast.makeText(this, "파일 생성 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
    
    private fun displayCapturedImage() {
        try {
            val imageView = findViewById<ImageView>(R.id.capturedImageView)
            imageView?.setImageURI(photoURI)
            imageView?.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(this, "이미지 표시 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

