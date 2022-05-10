package com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentDisplayMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DisplayMapFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentDisplayMapBinding

    private lateinit var mView: MapView
    lateinit var name: String
    lateinit var latLng: LatLng


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mapView = inflater.inflate(R.layout.fragment_display_map, container, false)

        //  try catch로 맵이 null이 아닌지 확인 필요

        mView = mapView.findViewById(R.id.map)
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this) // 구글맵을 불러오는 함수, this->MapsFragment->OnMapReadyCallback->onMapready()로 들어감


        return binding.root

        return mapView
    }

    override fun onMapReady(googleMap: GoogleMap) {  // onMapReady override, map에 위치 연동
        latLng = LatLng(37.3156, 126.804)
        val location: LatLng = LatLng(latLng.latitude, latLng.longitude) // 기본 위치 시흥 지정

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL //  지도의 종류 설정

        // 마커 출력 (자신의 위치: circle, 스팟의 위치: marker로 표시 필요)
//        var markerCircle: CircleOptions = CircleOptions()   // circle 생성
//                                            .center(location) //  circle의 중심
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("first location")
        )    //  지도에 Marker 표시

        googleMap.uiSettings.isZoomGesturesEnabled  //   줌 기능 활성화
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                15f
            )
        ) // 좌표변수와 줌의 정도 지정, 현재 위치로 이동
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))    //  줌 레벨 설정(굳이 두 개나 있어야 될까?)
    }

    
}