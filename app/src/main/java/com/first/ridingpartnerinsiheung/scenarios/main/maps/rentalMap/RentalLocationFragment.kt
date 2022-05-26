package com.first.ridingpartnerinsiheung.scenarios.main.maps.rentalMap

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RentalLocation
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker

class RentalLocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    private val rentalLocation = arrayListOf(
        RentalLocation("정왕 자전거 대여소", LatLng(37.343991285297, 126.74729588817)),
        RentalLocation("월곶 자전거 대여소", LatLng(37.3917953, 126.742692)))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rental_location, container, false)

        requirePermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap){

        this.naverMap = naverMap

        // 맵 타입
        naverMap.mapType = NaverMap.MapType.Navi
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)

        // 맵 시작 위치와 줌 설정
        val latLng = LatLng(37.349741467772, 126.76182486561)
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 11.0).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        //대여소 위치 추가
        addMarkers(naverMap)

        // 내 위치 받기
        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
    }

    private fun addMarkers(naverMap: NaverMap){
        rentalLocation.forEach{ place ->
            val marker = Marker()
            marker.position = LatLng(
                place.location.latitude,
                place.location.longitude)
            //marker.icon = OverlayImage.fromResource(R.drawable.))
            marker.map=naverMap
        }
    }

    //  권한 요청
    private val PERMISSION_CODE = 100

    private fun requirePermissions(){
        val permissions=arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        val isAllPermissionsGranted = permissions.all { //  permissions의 모든 권한 체크
            ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllPermissionsGranted) {    //  모든 권한이 허용되어 있을 경우
            //permissionGranted()
        } else { //  그렇지 않을 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_CODE)
        }
    }

    // 권한 요청 완료시 이 함수를 호출해 권한 요청에 대한 결과를 argument로 받을 수 있음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_CODE){
            if(grantResults.isNotEmpty()){
                for(grant in grantResults){
                    if(grant == PackageManager.PERMISSION_GRANTED) {
                        /*no-op*/
                    }else{
                        permissionDenied()
                        requirePermissions()
                    }
                }
            }
        }
    }
    // 권한이 없는 경우 실행
    private fun permissionDenied() {
        showToast("위치 권한이 필요합니다")
    }
}
