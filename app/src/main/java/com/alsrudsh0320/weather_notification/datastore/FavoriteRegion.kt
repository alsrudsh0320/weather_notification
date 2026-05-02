package com.alsrudsh0320.weather_notification.datastore

import com.alsrudsh0320.weather_notification.data_load.RegionPoint

data class FavoriteRegion(
    val region: RegionPoint,
    val isAlarmEnabled: Boolean = false,
    val alarmHour: Int? = null,
    val alarmMinute: Int? = null
)