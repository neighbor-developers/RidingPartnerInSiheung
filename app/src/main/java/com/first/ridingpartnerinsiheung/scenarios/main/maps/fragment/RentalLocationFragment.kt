package com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RentalLocation
import com.first.ridingpartnerinsiheung.scenarios.main.maps.RentalLocationXY
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class RentalLocationFragment : Fragment(), com.naver.maps.map.OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rental_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?

        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap){

        this.naverMap = naverMap
        val uiSettings = naverMap.uiSettings
        // 맵 첫 시작 카메라 위치
        val latLng =com.naver.maps.geometry.LatLng(37.349741467772, 126.76182486561)

        // 맵 타입
        naverMap.mapType = NaverMap.MapType.Navi
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)

        // 맵 시작 위치와 줌 설정
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 11.0)
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        //대여소 위치 추가
        addMarkers(naverMap)

        // 내 위치 받기
        uiSettings.isLocationButtonEnabled = true

        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            this.naverMap = naverMap

            locationSource = FusedLocationSource(this ,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            naverMap.locationSource = locationSource
            naverMap.locationTrackingMode = LocationTrackingMode.Face
        } else{
            Toast.makeText(requireContext(), "권한을 설정하세요", Toast.LENGTH_SHORT).show()
            permissionDenied()
        }
    }
    private val places:List<RentalLocation> by lazy{
        RentalLocationXY(requireActivity()).read()
    }

    private fun addMarkers(naverMap: NaverMap){
        places.forEach{ place ->
            val marker = Marker()
            marker.position = com.naver.maps.geometry.LatLng(
                place.location.latitude,
                place.location.longitude)
            //marker.icon = OverlayImage.fromResource(R.drawable.))
            marker.map=naverMap
        }
    }

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    //  권한 요청
    private fun requirePermissions(permissions: Array<String>){

        val isAllPermissionsGranted = permissions.all { //  permissions의 모든 권한 체크
            ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllPermissionsGranted) {    //  모든 권한이 허용되어 있을 경우
            //permissionGranted()
        } else { //  그렇지 않을 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    // 권한 요청 완료시 이 함수를 호출해 권한 요청에 대한 결과를 argument로 받을 수 있음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty()){
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied()
                    }else{
                        permissionGranted()
                    }
                }
            }
        }
    }
    // 권한이 있는 경우 실행
    private fun permissionGranted() {
        Toast.makeText(requireContext(), "위치 권한 수락 완료", Toast.LENGTH_SHORT).show() // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }
    // 권한이 없는 경우 실행
    private fun permissionDenied() {
        checkPermission()
    }
    private fun checkPermission(){
        // 사용할 권한 array로 저장
        val permissions=arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        // 권한 확인 및 요헝
        requirePermissions(permissions)
    }
}
