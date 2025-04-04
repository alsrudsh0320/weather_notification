package com.alsrudsh0320.weather_notification

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import android.Manifest.permission
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.alsrudsh0320.weather_notification.databinding.ActivityMainBinding
import com.alsrudsh0320.weather_notification.utils.Constants.TAG
import com.alsrudsh0320.weather_notification.weather_api_manager.WeatherApiManager
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        

        // 테스트 버튼을 눌렀을 때
        binding.btnTest.setOnClickListener {
            if (checkLocationPermission()) {
                lifecycleScope.launch {
                    val response = WeatherApiManager().fetchShortTermForecastData(this@MainActivity)
                    Log.d(TAG, "response: $response")
                }
            } else {
                // 권한이 없을 때, "다시 묻지 않음" 옵션 선택 여부에 따라 처리
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION)) {
                    showPermissionSettingsDialog()
                } else {
                    requestLocationPermission()
                }
            }
        }


    }// onCreate

    ///////////////// 여기서 부터 사용자 위치 권한 관련 ///////////////

    // 위치 권한 check
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 사용자에게 권한 요청 (다이얼로그)
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // 사용자가 권한 요청에 응답한 후 호출되는 콜백 함수
    // 일반적으로 사용자가 한번 허용한 후에는 호출 X
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    val response = WeatherApiManager().fetchShortTermForecastData(this@MainActivity)
                    Log.d(TAG, "response: $response")
                }

            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
                showPermissionSettingsDialog()
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("이 앱은 위치 권한이 필수적입니다. 권한을 수동으로 설정해주세요.")
            .setPositiveButton("설정으로 이동") { dialog, _ ->
                dialog.dismiss()
                // 앱 설정 화면으로 이동하는 인텐트
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    ///////////////// 여기 까지 사용자 위치 권한 관련 ///////////////

}