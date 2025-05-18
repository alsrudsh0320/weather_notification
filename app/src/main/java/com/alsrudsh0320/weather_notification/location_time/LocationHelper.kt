package com.alsrudsh0320.weather_notification.location_time

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.alsrudsh0320.weather_notification.data_load.RegionPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


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

    /**
     * 두 위경도 사이의 거리(km)를 구하는 하버사인 공식
     */
    fun haversine(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371.0  // 지구 반경 (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * userLat, userLon 에 가장 가까운 RegionPoint 하나를 찾아 반환
     */
    fun findNearestRegion(
        userLat: Double, userLon: Double,
        regions: List<RegionPoint>
    ): RegionPoint? {
        return regions.minByOrNull { rp ->
            haversine(userLat, userLon, rp.lat, rp.lon)
        }
    }
}



