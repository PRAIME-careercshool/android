package com.example.hackathon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DiaryCreateActivity : AppCompatActivity() {
    
    private lateinit var selectedActivities: MutableSet<String>
    private lateinit var photoURI: Uri
    private val CAMERA_PERMISSION_CODE = 100
    
    // 카메라 결과를 처리하는 ActivityResultLauncher
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(this, "사진이 촬영되었습니다!", Toast.LENGTH_SHORT).show()
            // 촬영한 이미지를 ImageView에 표시
            displayCapturedImage()
        } else {
            Toast.makeText(this, "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_create)
        
        selectedActivities = mutableSetOf()
        setupUI()
    }
    
    private fun setupUI() {
        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        // 재배지 선택
        setupCultivationAreaSelector()
        
        // 재배작물 선택
        setupCropSelector()
        
        // 재배활동 태그 선택
        setupActivityTags()
        
        // 농약 검색
        setupPesticideSearch()
        
        // 비료 검색
        setupFertilizerSearch()
        
        // 사진 업로드
        setupPhotoUpload()
        
        // 저장 버튼
        findViewById<CardView>(R.id.btnSave).setOnClickListener {
            saveDiary()
        }
        
        // 취소 버튼
        findViewById<CardView>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
    
    private fun setupCultivationAreaSelector() {
        val areaSelector = findViewById<LinearLayout>(R.id.areaSelector)
        areaSelector.setOnClickListener {
            // TODO: 재배지 선택 다이얼로그 또는 액티비티 호출
            Toast.makeText(this, "재배지 선택", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupCropSelector() {
        val cropSelector = findViewById<LinearLayout>(R.id.cropSelector)
        cropSelector.setOnClickListener {
            // TODO: 작물 선택 다이얼로그 또는 액티비티 호출
            Toast.makeText(this, "작물 선택", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupActivityTags() {
        val activityTags = listOf(
            "병해충관리", "비료영양관리", "파종 및 정식", "재배환경",
            "생장관리", "수확준비", "수확작업", "선별포장", "출하유통", "기타농작업"
        )
        
        activityTags.forEach { tag ->
            val tagView = findViewById<TextView>(when(tag) {
                "병해충관리" -> R.id.tag_pest_management
                "비료영양관리" -> R.id.tag_fertilizer_management
                "파종 및 정식" -> R.id.tag_planting
                "재배환경" -> R.id.tag_cultivation_environment
                "생장관리" -> R.id.tag_growth_management
                "수확준비" -> R.id.tag_harvest_preparation
                "수확작업" -> R.id.tag_harvest_work
                "선별포장" -> R.id.tag_sorting_packaging
                "출하유통" -> R.id.tag_shipping_distribution
                "기타농작업" -> R.id.tag_other_farm_work
                else -> return@forEach
            })
            
            tagView.setOnClickListener {
                toggleActivityTag(tag, tagView)
            }
        }
    }
    
    private fun toggleActivityTag(tag: String, tagView: TextView) {
        if (selectedActivities.contains(tag)) {
            selectedActivities.remove(tag)
            tagView.setBackgroundResource(R.drawable.rounded_input_background)
            tagView.setTextColor(resources.getColor(R.color.text_secondary, null))
        } else {
            selectedActivities.add(tag)
            tagView.setBackgroundResource(R.drawable.primary_button_background)
            tagView.setTextColor(resources.getColor(android.R.color.white, null))
        }
    }
    
    private fun setupPesticideSearch() {
        val pesticideSearch = findViewById<EditText>(R.id.pesticideSearch)
        pesticideSearch.setOnClickListener {
            // TODO: 농약 검색 기능 구현
            Toast.makeText(this, "농약 검색", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupFertilizerSearch() {
        val fertilizerSearch = findViewById<EditText>(R.id.fertilizerSearch)
        fertilizerSearch.setOnClickListener {
            // TODO: 비료 검색 기능 구현
            Toast.makeText(this, "비료 검색", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupPhotoUpload() {
        val photoUploadArea = findViewById<CardView>(R.id.photoUploadArea)
        photoUploadArea.setOnClickListener {
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
                Toast.makeText(this, "영농일지 사진 촬영을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
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
            val imageFileName = "DIARY_${timeStamp}_"
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
    
    private fun saveDiary() {
        // TODO: 일지 저장 로직 구현
        Toast.makeText(this, "일지가 저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }
}
