package com.alsrudsh0320.weather_notification.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/** ① DataStore<Preferences> 인스턴스를 Context 확장 프로퍼티로 선언 */
val Context.favoriteStore by preferencesDataStore(name = "favorite_regions")

/** ② 즐겨찾기 목록(JSON 직렬화 문자열)을 저장할 Key */
val FAVORITES_KEY = stringPreferencesKey("favorite_regions_json")