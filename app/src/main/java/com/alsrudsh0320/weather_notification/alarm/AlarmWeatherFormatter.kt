package com.alsrudsh0320.weather_notification.alarm

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AlarmWeatherFormatter {

    fun skyText(sky: String?, pty: String?): String {
        return when (pty) {
            "1" -> "비"
            "2" -> "비/눈"
            "3" -> "눈"
            "4" -> "소나기"
            else -> when (sky) {
                "1" -> "맑음"
                "3" -> "구름많음"
                "4" -> "흐림"
                else -> "날씨 상태 알 수 없음"
            }
        }
    }

    fun precipitationText(pty: String?): String? {
        return when (pty) {
            "1" -> "비"
            "2" -> "비/눈"
            "3" -> "눈"
            "4" -> "소나기"
            else -> null
        }
    }

    fun isPrecipitationPty(pty: String?): Boolean {
        return precipitationText(pty) != null
    }

    fun normalizeHour(fcstTime: String?): String? {
        if (fcstTime.isNullOrBlank()) return null

        return when {
            fcstTime.length >= 2 -> fcstTime.substring(0, 2)
            else -> null
        }
    }

    fun currentDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    fun currentHourTime(): String {
        val now = LocalDateTime.now()
        return "%02d00".format(now.hour)
    }
}