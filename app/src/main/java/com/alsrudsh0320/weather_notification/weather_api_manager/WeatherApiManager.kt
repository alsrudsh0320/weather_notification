package com.alsrudsh0320.weather_notification.weather_api_manager

import android.content.Context
import android.util.Log
import com.alsrudsh0320.weather_notification.data_load.RegionDataLoader
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.alsrudsh0320.weather_notification.location_time.LocationHelper
import com.alsrudsh0320.weather_notification.location_time.TimeHelper
import com.alsrudsh0320.weather_notification.model.ShortTermForecastResponseModel
import com.alsrudsh0320.weather_notification.retrofit.RetrofitManager
import com.alsrudsh0320.weather_notification.utils.API
import com.alsrudsh0320.weather_notification.utils.Constants.TAG

//  지역 정보와 예보를 한 번에 담아 리턴할 데이터 클래스
data class RegionForecast(
    val region: RegionPoint,
    val forecast: ShortTermForecastResponseModel?
)

class WeatherApiManager{

    companion object {
        val instance = WeatherApiManager()
    }

    // 단기예보
    // 현재 날짜, 시간, 사용자 위치정보를 가져와서 단기예보 API 호출 // 그 후 반환 값(response) return
    // 사용자가 검색으로 행정구역을 지정한 경우 해당 위치의 단기예보를 반환
    suspend fun fetchShortTermForecastData(context: Context, region: RegionPoint? = null): RegionForecast? {

        // 현재 Time 기반 baseDate, baseTime 추출
        val (baseDate, baseTime) = TimeHelper().getBaseDateTime()
        Log.d(TAG, "baseDate: $baseDate, baseTime: $baseTime")


        // 2) region이 null이면 현재 위치로부터 가장 가까운 region 찾기
        val targetRegion = region ?: run {

            // 사용자의 위치 정보 가져오기 (위도, 경도)
            val location = LocationHelper(context).getLastLocationSuspend()
                ?: run {
                    Log.e(TAG, "Location not available")
                    return null
                }

            // 사용자의 위치와 가장 가까운 region 반환
            LocationHelper(context).findNearestRegion(location.latitude, location.longitude, RegionDataLoader.loadRegions(context))
                ?: run {
                    Log.e(TAG, "Nearest region not found")
                    return null
                }

        }

        // API 호출
        val response = RetrofitManager.instance.getShortTermForecast(
            baseDate = baseDate,
            baseTime = baseTime,
            nx = targetRegion.nx,
            ny = targetRegion.ny,
            authKey = API.AUTH_KEY,
            pageNo = 1,
            numOfRows = 10000
        )

        return RegionForecast(targetRegion, response)
    }

}