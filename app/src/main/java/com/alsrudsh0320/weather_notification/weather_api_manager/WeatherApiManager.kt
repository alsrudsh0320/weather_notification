package com.alsrudsh0320.weather_notification.weather_api_manager

import android.content.Context
import android.util.Log
import com.alsrudsh0320.weather_notification.location_time.CoordinateConverter
import com.alsrudsh0320.weather_notification.location_time.LocationHelper
import com.alsrudsh0320.weather_notification.location_time.TimeHelper
import com.alsrudsh0320.weather_notification.model.ShortTermForecastResponseModel
import com.alsrudsh0320.weather_notification.retrofit.RetrofitManager
import com.alsrudsh0320.weather_notification.utils.API
import com.alsrudsh0320.weather_notification.utils.Constants.TAG


class WeatherApiManager{

    // 단기예보
    // 현재 날짜, 시간, 사용자 위치정보를 가져와서 단기예보 API 호출 // 그 후 반환 값(response) return
    suspend fun fetchShortTermForecastData(context: Context): ShortTermForecastResponseModel? {

        // 현재 Time 기반 baseDate, baseTime 추출
        val (baseDate, baseTime) = TimeHelper().getBaseDateTime()
        Log.d(TAG, "baseDate: $baseDate, baseTime: $baseTime")

        // 위치 정보 확인
        val location = LocationHelper(context).getLastLocationSuspend()
        if (location == null) {
            Log.e(TAG, "Location not available")
            return null
        }

        // 격자 좌표로 변환
        val xyData = CoordinateConverter().convertToXy(location.latitude, location.longitude)
        Log.d(TAG, "nx: ${xyData.nx}, ny: ${xyData.ny}")

        // API 호출
        val response = RetrofitManager.instance.getShortTermForecast(
            baseDate = baseDate,
            baseTime = baseTime,
            nx = xyData.nx,
            ny = xyData.ny,
            authKey = API.AUTH_KEY,
            pageNo = 1,
            numOfRows = 10000
        )

        return response
    }

}