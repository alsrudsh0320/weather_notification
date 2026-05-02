package com.alsrudsh0320.weather_notification.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.alsrudsh0320.weather_notification.datastore.FavoriteRepository
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FavoriteRepository

    // Application 컨텍스트를 getApplication() 으로 꺼내서 사용
    val favorites = repo
        .getFavorites(getApplication())
        .asLiveData()

    /** ViewModelScope 내 coroutine 에서 suspend 함수 호출 */
    fun toggle(region: RegionPoint) = viewModelScope.launch {
        repo.toggleFavorite(getApplication(), region)
    }
    fun updateAlarm(
        regionCode: Long,
        isAlarmEnabled: Boolean,
        alarmHour: Int?,
        alarmMinute: Int?
    ) = viewModelScope.launch {
        repo.updateAlarm(
            getApplication(),
            regionCode,
            isAlarmEnabled,
            alarmHour,
            alarmMinute
        )
    }
}