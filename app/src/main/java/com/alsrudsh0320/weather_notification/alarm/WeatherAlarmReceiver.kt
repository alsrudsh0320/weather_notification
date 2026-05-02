package com.alsrudsh0320.weather_notification.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alsrudsh0320.weather_notification.R
import com.alsrudsh0320.weather_notification.data_load.RegionDataLoader
import com.alsrudsh0320.weather_notification.weather_api_manager.WeatherApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val regionCode = intent.getLongExtra("regionCode", -1L)
        if (regionCode == -1L) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val regions = RegionDataLoader.loadRegions(context)
                val region = regions.firstOrNull { it.regionCode == regionCode }

                if (region == null) {
                    NotificationHelper.showWeatherAlarmNotification(
                        context = context,
                        regionCode = regionCode,
                        title = "날씨 알림",
                        message = "지역 정보를 찾지 못했습니다.",
                        iconResId = R.drawable.wb_sunny_24
                    )
                    return@launch
                }

                val response = WeatherApiManager.instance.fetchShortTermForecastData(
                    context = context,
                    region = region
                )

                val forecastResponse = response?.response

                if (forecastResponse == null) {
                    NotificationHelper.showWeatherAlarmNotification(
                        context = context,
                        regionCode = regionCode,
                        title = "${region.regionLevel2} ${region.regionLevel3} 날씨",
                        message = "날씨 정보를 가져오지 못했습니다.",
                        iconResId = R.drawable.wb_sunny_24
                    )
                    return@launch
                }

                val alarmInfo = WeatherApiManager.instance.buildAlarmWeatherInfo(forecastResponse)

                NotificationHelper.showWeatherAlarmNotification(
                    context = context,
                    regionCode = regionCode,
                    title = "${region.regionLevel2} ${region.regionLevel3} 날씨",
                    message = alarmInfo.toNotificationText(),
                    iconResId = alarmInfo.iconResId
                )
            } catch (e: Exception) {
                NotificationHelper.showWeatherAlarmNotification(
                    context = context,
                    regionCode = regionCode,
                    title = "날씨 알림",
                    message = "날씨 알림을 불러오는 중 오류가 발생했습니다.",
                    iconResId = R.drawable.wb_sunny_24
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}