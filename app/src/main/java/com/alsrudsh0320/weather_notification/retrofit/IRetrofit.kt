package com.alsrudsh0320.weather_notification.retrofit

import com.alsrudsh0320.weather_notification.model.ShortTermForecastResponseModel
import com.alsrudsh0320.weather_notification.utils.API
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    @GET(API.SHORT_TERM_FORECAST)
    suspend fun getShortTermForecast(
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
        @Query("authKey") authKey: String
    ): ShortTermForecastResponseModel

}