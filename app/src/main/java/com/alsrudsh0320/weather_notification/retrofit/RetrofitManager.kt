package com.alsrudsh0320.weather_notification.retrofit

import android.util.Log
import com.alsrudsh0320.weather_notification.model.ShortTermForecastResponseModel
import com.alsrudsh0320.weather_notification.utils.API
import com.alsrudsh0320.weather_notification.utils.Constants.TAG

class RetrofitManager {

    // 싱글턴 적용
    // 레트로핏 메니저를 가져올 때 인스턴스만 가져온다
    companion object {
        val instance = RetrofitManager()
    }

    // 레트로핏 인터페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    // suspend 함수를 통해 API 호출을 수행하고, 결과를 반환합니다.
    suspend fun getShortTermForecast(
        baseDate: String,   // 예: "20250330"
        baseTime: String,   // 예: "2000"
        nx: Int,            // 예: 55
        ny: Int,            // 예: 127
        authKey: String,
        pageNo: Int,
        numOfRows: Int,
        dataType: String = "JSON"
    ): ShortTermForecastResponseModel? {
        return try {
            iRetrofit?.getShortTermForecast(
                pageNo = pageNo,
                numOfRows = numOfRows,
                dataType = dataType,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny,
                authKey = authKey
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching short term forecast", e)
            null
        }
    }


}