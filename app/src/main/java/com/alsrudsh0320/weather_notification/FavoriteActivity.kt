package com.alsrudsh0320.weather_notification

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsrudsh0320.weather_notification.alarm.WeatherAlarmScheduler
import com.alsrudsh0320.weather_notification.databinding.ActivityFavoriteBinding
import com.alsrudsh0320.weather_notification.layout_adapter.FavoriteRegionAdapter

import com.alsrudsh0320.weather_notification.viewModel.MainViewModel
import java.util.Calendar

class FavoriteActivity : BaseActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var favoriteRegionAdapter: FavoriteRegionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge(binding.root) // baseActivity


        // 툴바 뒤로가기 버튼 클릭 리스너
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        favoriteRegionAdapter = FavoriteRegionAdapter(
            items = emptyList(),
            onRegionClick = { region ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("regionCode", region.region.regionCode)
                }
                startActivity(intent)
                finish()
            },
            onAlarmClick = { favoriteRegion ->


                if (favoriteRegion.isAlarmEnabled) {
                    viewModel.updateAlarm(
                        regionCode = favoriteRegion.region.regionCode,
                        isAlarmEnabled = false,
                        alarmHour = null,
                        alarmMinute = null
                    )
                    WeatherAlarmScheduler.cancelAlarm(
                        this,
                        favoriteRegion.region.regionCode
                    )
                } else {
                    if (!ensureNotificationPermission()) return@FavoriteRegionAdapter // 알림 권한 확인
                    
                    val calendar = Calendar.getInstance()
                    val defaultHour = favoriteRegion.alarmHour ?: calendar.get(Calendar.HOUR_OF_DAY)
                    val defaultMinute = favoriteRegion.alarmMinute ?: calendar.get(Calendar.MINUTE)

                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->

                            if (!ensureNotificationPermission()) return@TimePickerDialog

                            val scheduled = WeatherAlarmScheduler.scheduleAlarm(
                                this,
                                favoriteRegion.region.regionCode,
                                hourOfDay,
                                minute
                            )

                            if (scheduled) {
                                viewModel.updateAlarm(
                                    regionCode = favoriteRegion.region.regionCode,
                                    isAlarmEnabled = true,
                                    alarmHour = hourOfDay,
                                    alarmMinute = minute
                                )
                            } else {
                                viewModel.updateAlarm(
                                    regionCode = favoriteRegion.region.regionCode,
                                    isAlarmEnabled = false,
                                    alarmHour = null,
                                    alarmMinute = null
                                )

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = Uri.parse("package:$packageName")
                                    }
                                    startActivity(intent)
                                }
                            }
                        },
                        defaultHour,
                        defaultMinute,
                        true
                    ).show()
                }

            }
        )


        binding.rvFavoriteList.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = favoriteRegionAdapter
        }

        viewModel.favorites.observe(this) { list ->
            favoriteRegionAdapter.update(list)
        }
    }
}