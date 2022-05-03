package com.first.ridingpartnerinsiheung.scenarios.map

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.GoogleMap

class LocationGPS {
    private lateinit var mMap: GoogleMap    //  현재 위치를 검색하기 위해
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치값 사용
    //FusedLocationProviderClient API 사용시 GPS 신호 및 와이파이와 통신사 네트워크 위치를 결합해 최소한의 배터리 사용량으로 빠르고 정확한 위치 검색
    private lateinit var locationCallback: LocationCallback //  위치값 요청에 대한 갱신 정보를 받아옴

}