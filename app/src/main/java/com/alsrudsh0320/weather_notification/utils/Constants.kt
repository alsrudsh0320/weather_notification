package com.alsrudsh0320.weather_notification.utils

import com.alsrudsh0320.weather_notification.BuildConfig

object Constants {
    const val TAG: String = "로그"
}

object API {
    const val BASE_URL : String = "https://apihub.kma.go.kr"

    const val AUTH_KEY : String = BuildConfig.AUTH_KEY

    const val SHORT_TERM_FORECAST : String = "/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst"
}