package com.alsrudsh0320.weather_notification.data_load

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RegionDataLoader {

    // 한 번만 로드하도록 캐싱
    private var cachedRegions: List<RegionPoint>? = null

    fun loadRegions(context: Context): List<RegionPoint> {
        // 이미 로드된 적 있으면 그대로 반환
        cachedRegions?.let { return it }

        // assets 폴더에서 JSON 파일 읽기
        val json = context.assets.open("region_points.json")
            .bufferedReader()
            .use { it.readText() }

        // Gson을 이용해 List<RegionPoint> 로 파싱
        val listType = object : TypeToken<List<RegionPoint>>() {}.type
        val regions: List<RegionPoint> = Gson().fromJson(json, listType)

        // 캐시에 보관
        cachedRegions = regions
        return regions
    }
}