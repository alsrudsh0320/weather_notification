package com.alsrudsh0320.weather_notification.data_load

import com.google.gson.annotations.SerializedName


data class RegionPoint(
    @SerializedName("1단계") val regionLevel1: String,     // 지역 레벨1: 시/도 단위 (예: "서울특별시")
    @SerializedName("2단계") val regionLevel2: String,     // 지역 레벨2: 시/군/구 단위 (예: "중구"), 없으면 빈 문자열
    @SerializedName("3단계") val regionLevel3: String,     // 지역 레벨3: 동 단위 (예: "태평로1가"), 없으면 빈 문자열
    @SerializedName("격자 X") val nx: Int,                  // 기상청 격자 좌표 X
    @SerializedName("격자 Y") val ny: Int,                  // 기상청 격자 좌표 Y
    @SerializedName("경도(초/100)") val lon: Double,       // 소수점 경도 (초 단위 / 100)
    @SerializedName("위도(초/100)") val lat: Double         // 소수점 위도 (초 단위 / 100)
)
