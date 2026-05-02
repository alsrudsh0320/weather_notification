package com.alsrudsh0320.weather_notification.datastore


import com.google.gson.reflect.TypeToken

// Gson.fromJson(json, regionListType) 등에 사용할 타입
val favoriteRegionListType  = object : TypeToken<List<FavoriteRegion>>() {}.type