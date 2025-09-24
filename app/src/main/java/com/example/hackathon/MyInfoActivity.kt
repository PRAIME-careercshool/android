package com.example.hackathon

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MyInfoActivity : AppCompatActivity() {

    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var btnSetNotification: CardView
    private lateinit var btnEditFarmInfo: LinearLayout
    private lateinit var btnAppSettings: LinearLayout
    private lateinit var btnBack: ImageButton
    private lateinit var locationText: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            requestLocationUpdate()
        } else {
            Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        initViews()
        setupSpinners()
        setupClickListeners()
        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initViews() {
        hourSpinner = findViewById(R.id.hourSpinner)
        minuteSpinner = findViewById(R.id.minuteSpinner)
        btnSetNotification = findViewById(R.id.btnSetNotification)
        btnEditFarmInfo = findViewById(R.id.btnEditFarmInfo)
        btnAppSettings = findViewById(R.id.btnAppSettings)
        btnBack = findViewById(R.id.btnBack)
        locationText = findViewById(R.id.locationText)
    }

    private fun setupSpinners() {
        val hours = (0..23).map { String.format("%02d", it) }
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hourSpinner.adapter = hourAdapter
        hourSpinner.setSelection(9)

        val minutes = listOf("00", "15", "30", "45")
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        minuteSpinner.adapter = minuteAdapter
        minuteSpinner.setSelection(0)

        hourSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                updateNotificationButtonText()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        minuteSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                updateNotificationButtonText()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun updateNotificationButtonText() {
        val selectedHour = hourSpinner.selectedItem.toString()
        val selectedMinute = minuteSpinner.selectedItem.toString()
        val buttonText = "매일 ${selectedHour}:${selectedMinute}에 알림"
        findViewById<TextView>(R.id.notificationButtonText).text = buttonText
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSetNotification.setOnClickListener {
            val selectedHour = hourSpinner.selectedItem.toString()
            val selectedMinute = minuteSpinner.selectedItem.toString()
            val message = "알림이 ${selectedHour}:${selectedMinute}으로 설정되었습니다"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        btnEditFarmInfo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("농장 위치 설정")
                .setMessage("현재 위치를 농장으로 지정하겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    requestLocationUpdate()
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        btnAppSettings.setOnClickListener {
            Toast.makeText(this, "앱 설정 페이지로 이동", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    updateAddress(location)
                } else {
                    Toast.makeText(this, "위치 정보를 가져올 수 없습니다. GPS를 활성화해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun updateAddress(location: android.location.Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val geocoder = Geocoder(this, Locale.KOREA)
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0)
                    runOnUiThread {
                        locationText.text = addressText
                        Toast.makeText(this, "농장 위치가 '$addressText'(으)로 업데이트되었습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val geocoder = Geocoder(this, Locale.KOREA)
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0)
                    locationText.text = addressText
                    Toast.makeText(this, "농장 위치가 '$addressText'(으)로 업데이트되었습니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "주소 변환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "farm_diary_notification"
            val channelName = "영농일지 알림"
            val channelDescription = "매일 정해진 시간에 영농일지 작성을 알려주는 채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}