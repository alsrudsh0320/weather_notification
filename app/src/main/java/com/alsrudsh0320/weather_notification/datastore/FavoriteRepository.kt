package com.alsrudsh0320.weather_notification.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FavoriteRepository {

    private val gson = Gson()

    /** 즐겨찾기 목록을 Flow<List<RegionPoint>> 로 읽어옵니다. */
    fun getFavorites(context: Context): Flow<List<FavoriteRegion >> =
        context.favoriteStore.data
            .map { prefs ->
                prefs[FAVORITES_KEY]
                    // JSON 문자열이 있으면 List<FavoriteRegion> 로 파싱
                    ?.let { gson.fromJson<List<FavoriteRegion>>(it, favoriteRegionListType ) }
                // 없으면 빈 리스트
                    ?: emptyList()
            }

    /** 해당 RegionPoint 를 추가/제거(touch) 합니다. */
    suspend fun toggleFavorite(context: Context, region: RegionPoint) {
        context.favoriteStore.edit { prefs ->
            // 1) 현재 저장된 리스트
            val currentList: List<FavoriteRegion> =
                prefs[FAVORITES_KEY]?.let { gson.fromJson(it, favoriteRegionListType) }
                    ?: emptyList()

            // 2) 토글 로직: 이미 있으면 제거, 없으면 추가
            val newList = if (currentList.any { it.region.regionCode == region.regionCode }) {
                currentList.filterNot { it.region.regionCode == region.regionCode }
            } else {
                currentList + FavoriteRegion(region = region)
            }

            // 3) JSON 으로 직렬화해서 저장
            prefs[FAVORITES_KEY] = gson.toJson(newList)
        }
    }

    suspend fun updateAlarm(
        context: Context,
        regionCode: Long,
        isAlarmEnabled: Boolean,
        alarmHour: Int?,
        alarmMinute: Int?
    ) {
        context.favoriteStore.edit { prefs ->
            val currentList: List<FavoriteRegion> =
                prefs[FAVORITES_KEY]
                    ?.let { gson.fromJson(it, favoriteRegionListType) }
                    ?: emptyList()

            val newList = currentList.map { favorite ->
                if (favorite.region.regionCode == regionCode) {
                    favorite.copy(
                        isAlarmEnabled = isAlarmEnabled,
                        alarmHour = alarmHour,
                        alarmMinute = alarmMinute
                    )
                } else {
                    favorite
                }
            }

            prefs[FAVORITES_KEY] = gson.toJson(newList)
        }
    }
}