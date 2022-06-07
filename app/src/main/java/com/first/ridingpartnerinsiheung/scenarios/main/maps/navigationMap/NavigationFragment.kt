package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.api.bikepath.ApiObject2
import com.first.ridingpartnerinsiheung.api.bikepath.Path
import com.first.ridingpartnerinsiheung.api.place.ApiObject
import com.first.ridingpartnerinsiheung.api.place.PlaceDetail
import com.first.ridingpartnerinsiheung.databinding.FragmentNavigationBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap.RidingViewModel
import com.first.ridingpartnerinsiheung.scenarios.main.maps.routeSearchPage.PlaceAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Response

class NavigationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNavigationBinding
    private val viewModel by viewModels<RidingViewModel>()

    private lateinit var mNaverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var locationManager: LocationManager? = null

    lateinit var startPlaceData: PlaceDetail.Place;
    lateinit var endPlaceData: PlaceDetail.Place;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initBinding()
        initMapView()
        initObserves()
        initMapView()

        return binding.root
    }
    private fun initMapView(){
        locationSource = FusedLocationSource(this, PERMISSION_CODE)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.navigationMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.navigationMapView, it).commit()
            }
        mapFragment?.getMapAsync(this)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initObserves(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.navigationEvent.collect(){ event ->
                when(event){
                    RidingViewModel.NavigationEvent.StartNavigation -> startNavigation()
                }
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.locationSource = locationSource
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow

        setOverlay()
    }

    private fun setOverlay() {
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.subIcon =
            OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_location_overlay_icon)
        locationOverlay.subIconWidth = 40
        locationOverlay.subIconHeight = 40
        locationOverlay.subAnchor = PointF(0.5f, 0.5f)
    }

    // 시작지점 마크
    private fun marker(
        latLng: LatLng,
        title: String
    ): Marker {
        val marker = Marker()
        marker.position = latLng
        marker.map = mNaverMap
        marker.width = 50
        marker.height = 50
        marker.captionText = title
        return marker
    }

    private fun startNavigation(){ // 시작버튼
        // binding.startBtn.visibility = View.GONE
        var startTime = viewModel.getTimeNow() // 시작 시간
        var startLatLng = LatLng(startPlaceData.y.toDouble(), startPlaceData.x.toDouble())
        var startMarker = marker(startLatLng, "출발지점") // 출발지점 마크

        var endLatLng = LatLng(endPlaceData.y.toDouble(), endPlaceData.x.toDouble())

        var path = getPath();
        setOverlay()

        viewModel.befLatLng = startLatLng
        viewModel.calDisSpeed() // 속도, 거리, 주행시간 계산 시작
    }

    private fun getPath(): Path? {
        val call = ApiObject2.retrofitService.getPAth(
            start = "126.9820673,37.4853855,name=이수역 7호선",
            destination = "126.9803409,37.5029146,name=동작역 4호선",
        )

        var path: Path? = null;

        call.enqueue(object : retrofit2.Callback<Path> {
            override fun onResponse(call: Call<Path>, response: Response<Path>) {
                if (response.isSuccessful) {
                    try {
                        Log.d("확인", "성공")
                        path = response.body()!!
                    }catch (e : NullPointerException){
                        Log.d("에러러", "dd")
                    }
                } else {
                    Log.d("확인", "에러")
                }
            }

            override fun onFailure(call: Call<Path>, t: Throwable) {
                Log.d("에러", "ㅜㅜ")
            }
        })

        return path
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
