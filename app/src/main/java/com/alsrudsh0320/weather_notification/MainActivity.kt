package com.alsrudsh0320.weather_notification

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.alsrudsh0320.weather_notification.databinding.ActivityMainBinding
import com.alsrudsh0320.weather_notification.retrofit.RetrofitManager
import com.alsrudsh0320.weather_notification.utils.API
import com.alsrudsh0320.weather_notification.utils.Constants.TAG
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

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

            lifecycleScope.launch {
                val response = RetrofitManager.instance.getShortTermForecast(
                    baseDate = "20250330",
                    baseTime = "2000",
                    nx = 55,
                    ny = 127,
                    authKey = API.AUTH_KEY
                )

                if (response != null) {
                    Log.d(TAG, "API Response: $response")
                    // UI 업데이트 등 처리
                } else {
                    Log.e(TAG, "API call failed")
                }
            }

        }

    }
}