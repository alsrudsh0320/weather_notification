package com.alsrudsh0320.weather_notification.weather_api_manager

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import com.alsrudsh0320.weather_notification.R
import com.alsrudsh0320.weather_notification.data_load.RegionDataLoader
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.alsrudsh0320.weather_notification.layout_adapter.ShortTermForecastItem
import com.alsrudsh0320.weather_notification.location_time.LocationHelper
import com.alsrudsh0320.weather_notification.location_time.TimeHelper
import com.alsrudsh0320.weather_notification.model.Item
import com.alsrudsh0320.weather_notification.model.ShortTermForecastResponseModel
import com.alsrudsh0320.weather_notification.retrofit.RetrofitManager
import com.alsrudsh0320.weather_notification.utils.API
import com.alsrudsh0320.weather_notification.utils.Constants.TAG
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.SortedMap

//  지역 정보와 예보를 한 번에 담아 리턴할 데이터 클래스
data class RegionForecast(
    val region: RegionPoint,
    val response: ShortTermForecastResponseModel?
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

    /**
     * ShortTermForecastResponseModel 에서 UI 에 바인딩할 List<ShortTermForecastItem> 을 만들어 줍니다.
     */
    data class ForecastResult(
        val hourly: List<ShortTermForecastItem>,
        val tmn: String?,    // 최저기온 원값 (예: "5")
        val tmx: String?     // 최고기온 원값 (예: "18")
    )
    /** buildForecastItems 함수입니다.*/
    fun buildForecastItems(
        response: ShortTermForecastResponseModel
    ): ForecastResult {
        // 1) 예보 아이템 리스트 추출
        val raw = response.response.body.items.item
        // 오늘 날짜 문자열: "yyyyMMdd"
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        // 2) 오늘 날짜에 해당하는 TMN/TMX만 추출
        val tmnValue = raw
            .firstOrNull { it.category == "TMN" && it.fcstDate == todayStr }
            ?.fcstValue

        val tmxValue = raw
            .firstOrNull { it.category == "TMX" && it.fcstDate == todayStr }
            ?.fcstValue



        // 2) 시간별로 그룹핑 (fcstTime 기준, 오름차순 정렬)
        val grouped : SortedMap<String, List<Item>> = raw
            // .filter { it.category !in setOf("TMN", "TMX") } // 로그 확인위해 잠깐 빼둠
            .groupBy {
            it.fcstDate + it.fcstTime
        }.toSortedMap()

        Log.d(TAG, "MainActivity - buildForecastItems() called - grouped:\n ${grouped.entries
            .joinToString(separator = "\n\n") { (time, items) ->
                buildString {
                    append("▶ 예보 시간: $time\n")
                    // 각 아이템을 한 줄씩
                    items.forEach { item ->
                        append("    • $item\n")
                    }
                }
            }}")

        val hourly = grouped.map { (time, list) ->
            // time: "0900", "1200" 등 -> "09:00" 으로 포맷

            val dateStr = time.substring(0, 8) // "YYYYMMDD"
            val hh = time.substring(8, 10)

            val forecastDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"))
            val today = LocalDate.now()

            val displayPrefix = when (forecastDate) {
                today -> "오늘"
                today.plusDays(1) -> "내일"
                today.plusDays(2) -> "모레"
                else -> forecastDate.format(DateTimeFormatter.ofPattern("dd일"))
            }

            val displayTime = "$displayPrefix\n${hh}시"

            // 카테고리별 값 꺼내기 (없으면 기본값)
            fun findValue(cat: String) = list
                .firstOrNull { it.category == cat }
                ?.fcstValue
                .orEmpty()

            val skyCode = findValue("SKY")
            val tmp = findValue("TMP")
            val pop = findValue("POP")

            val pcpOrSno = findValue("SNO")
                .takeUnless { it.isBlank() || it == "-" || it == "0" || it == "적설없음" }
                ?: run {
                    findValue("PCP")
                        .takeUnless { it.isBlank() || it == "-" || it == "0" }   // takeUnless : 조건이 false일 때만 원값 넘김
                        ?: "강수없음"
                }
            val reh = findValue("REH")
            val wsd = findValue("WSD")


            // 하늘 상태 아이콘 매핑 함수
            @DrawableRes
            fun iconForSky(code: String): Int = when (code) {
                "1" -> R.drawable.wb_sunny_24               // 맑음(1)
                "3" -> R.drawable.outline_cloud_24          // 구름많음(3)
                "4" -> when (findValue("PTY")) {
                    // PTY : 강수형태코드
                    "0" -> R.drawable.outline_cloud_24      // 강수없음(0), SKY: 흐림(4)
                    "1" -> R.drawable.rainy_24              // 비(1)
                    "2" -> R.drawable.rainy_24              // 비/눈(2)
                    "3" -> R.drawable.baseline_ac_unit_24   // 눈(3)
                    "4" -> R.drawable.rainy_heavy_24        // 소나기(4)
                    else -> R.drawable.wb_sunny_24
                }

                else -> R.drawable.wb_sunny_24
            }

            // 최저 최고 기온 꺼내기

            ShortTermForecastItem(
                time = displayTime,                   // 시간
                skyIconRes = iconForSky(skyCode),    // 하늘 상태 아이콘
                tmp = tmp,                     // 1시간 기온
                pop = pop,                    // 강수확률
                pcp = pcpOrSno,               // 1시간 강수량 or 1시간 신적설
                reh = reh,                    // 습도
                wsd = wsd                     // 풍속
            )
        }

        // 3) 각 시간별 그룹을 ShortTermForecastItem 으로 매핑
        return ForecastResult(
            hourly = hourly,
            tmn = tmnValue,
            tmx = tmxValue
        )
    }

}