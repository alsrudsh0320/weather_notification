package com.alsrudsh0320.weather_notification.location_time

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    // FusedLocationProviderClient 인스턴스 생성
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 마지막 위치를 가져오는 함수 (권한 체크는 호출 전에 수행)
    @SuppressLint("MissingPermission")
    fun getLastLocation(onLocationResult: (Location?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onLocationResult(location)
            }
            .addOnFailureListener {
                onLocationResult(null)
            }
    }

    // 외부 함수에서 suspend를 사용하기 위한 확장함수
    @SuppressLint("MissingPermission")
    suspend fun getLastLocationSuspend(): Location? =
        suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
}



