package com.alsrudsh0320.weather_notification.location_time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class TimeHelper {

    fun getBaseDateTime(): Pair<String, String> {
        val now = LocalDateTime.now()
        // 기상청이 제공하는 baseTime 후보 목록 (시간)
        val baseTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)

        var selectedTime: LocalDateTime? = null
        // 오늘의 각 후보 시간에 대해, "candidate + 10분" 즉, 발표 시각을 기준으로 판단
        for (baseHour in baseTimes) {
            // 오늘의 candidate: baseHour:00
            val candidate = now.withHour(baseHour).withMinute(0).withSecond(0).withNano(0)
            // 발표 시각: candidate + 10분
            val threshold = candidate.plusMinutes(12)
            if (now >= threshold) {
                // 현재 시간이 threshold 이상이면, candidate를 사용 가능한 값으로 갱신
                selectedTime = candidate
            } else {
                // 현재 시간이 threshold 이전이면, 더 큰 시간은 아직 준비되지 않았으므로 반복 종료
                break
            }
        }

        // 만약 오늘의 어느 candidate도 조건에 부합하지 않으면(예: 오전 01시, 02시 등)
        // 전날의 마지막 baseTime(23시)를 사용
        if (selectedTime == null) {
            val yesterday = now.minusDays(1)
            selectedTime = yesterday.withHour(23).withMinute(0).withSecond(0).withNano(0)
        }

        // API에 전달할 형식으로 포맷 (예: "yyyyMMdd"와 "HHmm")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timeFormatter = DateTimeFormatter.ofPattern("HHmm")
        val baseDate = selectedTime!!.format(dateFormatter)
        val baseTime = selectedTime.format(timeFormatter)

        return Pair(baseDate, baseTime)
    }
}