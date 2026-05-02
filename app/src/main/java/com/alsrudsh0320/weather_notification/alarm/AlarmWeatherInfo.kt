package com.alsrudsh0320.weather_notification.alarm

import androidx.annotation.DrawableRes

data class AlarmWeatherInfo(
    val currentTemp: String?,
    val maxTemp: String?,
    val skyText: String,
    val precipitationStartHour: String?,
    val precipitationTypeText: String?,
    @DrawableRes val iconResId: Int
) {
    fun toNotificationText(): String {
        val tempText = "현재 ${currentTemp ?: "--"}°C / 최고 ${maxTemp ?: "--"}°C"

        val precipitationText =
            if (precipitationStartHour != null && precipitationTypeText != null) {
                "${precipitationStartHour}시 ${precipitationTypeText} 예정"
            } else {
                "강수 예정 없음"
            }

        return "$tempText\n$skyText / $precipitationText"
    }
}