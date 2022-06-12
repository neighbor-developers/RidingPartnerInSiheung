package com.first.ridingpartnerinsiheung.scenarios.main.maps.rentalMap

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RentalLocation
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay

class RentalLocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var markers: List<Marker>

    private val rentalLocation = arrayListOf(
        RentalLocation("정왕 자전거 대여소", LatLng(37.343991285297, 126.74729588817)),
        RentalLocation("월곶 자전거 대여소", LatLng(37.3917953, 126.742692)))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            initClickListener()
        }, 500)
        return inflater.inflate(R.layout.fragment_rental_location, container, false)

        requirePermissions()
    }

    private fun initClickListener(){
        val infoWindowAtjeongWang = InfoWindow()
        infoWindowAtjeongWang.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "월 ~ 금\n" +
                        "(07시 ~ 21시)\n" +
                        "토요일, 일요일, 공휴일 휴무\n" +
                        "☎ 031-433-0101"
            }
        }

        val infoWindowAtWolgot = InfoWindow()
        infoWindowAtWolgot.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "수 ~ 일\n" +
                        "(09시 ~ 20시)\n" +
                        "월요일, 화요일, 공휴일 휴무\n" +
                        "☎ 031-433-0101"
            }
        }

        var infoWindows = listOf(infoWindowAtjeongWang, infoWindowAtWolgot)

        // 지도 클릭시 정보창 제거
        naverMap.setOnMapClickListener {
                a, b -> infoWindows.forEach {it.close()}
        }

        // 마커를 클릭하면:
        val listener = infoWindows.map {Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                it.open(marker)
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                it.close()
            }
            true
        }}

        markers[0].onClickListener = listener[0]
        markers[1].onClickListener = listener[1]
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
        markers = addMarkers(naverMap, rentalLocation)

        // 내 위치 받기
        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
    }

    private fun addMarkers(naverMap: NaverMap, place: List<RentalLocation>): List<Marker> {
        return place.map {
            val marker = Marker()
            marker.position = LatLng(
                it.location.latitude,
                it.location.longitude)
            marker.captionText = it.name
            marker.map=naverMap

            marker
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
