package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.place_list_item.view.*
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Response
import java.lang.NullPointerException

class NavigationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mNaverMap: NaverMap

    private var ridingState = false // 라이딩 상태
    private var startLatLng = LatLng(0.0, 0.0) // polyline 시작점
    private var endLatLng = LatLng(0.0, 0.0) // polyline 끝점

    private lateinit var binding: FragmentNavigationBinding
    private val viewModel by viewModels<RidingViewModel>()

    private lateinit var locationSource: FusedLocationSource

    private var locationManager: LocationManager? = null

    private var startTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding()
        initObserves()

        locationSource = FusedLocationSource(this, PERMISSION_CODE)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.ridingMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.ridingMapView, it).commit()
            }
        mapFragment?.getMapAsync(this)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return binding.root
    }

    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
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
        locationOverlay.subIconWidth = 80
        locationOverlay.subIconHeight = 40
        locationOverlay.subAnchor = PointF(0.5f, 1f)
    }

    private fun changeLocation() {
        mNaverMap.addOnLocationChangeListener { location ->
            if (mNaverMap.locationTrackingMode == LocationTrackingMode.NoFollow) {
                setOverlay()
            }
            viewModel.mLocation.value = location
        }
    }
    private fun startRiding(){ // 시작버튼
        ridingState = true // 라이딩중
        startTime = viewModel.getTimeNow() // 시작 시감

        locationSource.lastLocation?.let { location ->
            startLatLng =
                LatLng(location.latitude, location.longitude)
            var startMarker = marker(startLatLng, "출발지점") // 출발지점 마크
        }
        changeLocation()

        setOverlay()

        viewModel.befLatLng = startLatLng
        viewModel.calDisSpeed() // 속도, 거리, 주행시간 계산 시작
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

    private fun initObserves(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.navigationEvent.collect(){ event ->
                when(event){
                    RidingViewModel.NavigationEvent.SetStartPlace -> setPlace("start")
                    RidingViewModel.NavigationEvent.SetEndPlace -> setPlace("end")
                }
            }
        }
    }

    lateinit var startPlaceData: PlaceDetail.Place;
    lateinit var endPlaceData: PlaceDetail.Place;

    private fun setPlace(type: String) {
        var place = binding.startPlace.text.toString()
        if (type == "start") {
            place = binding.endPlace.text.toString()
        }

        val call = ApiObject.retrofitService.getPlaces(
            coords = "37.5055196999994,126.94229594606628",
            query = place)

        call.enqueue(object : retrofit2.Callback<PlaceDetail> {
            override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
                if (response.isSuccessful) {
                    try {
                        val placeDetail: List<PlaceDetail.Place> = response.body()!!.place
                        Log.d("확인", "해치웠나")

                        val placeAdapter = PlaceAdapter(requireContext(), placeDetail!!)
                        binding.placeListView.adapter = placeAdapter
                        binding.placeListView.visibility = View.VISIBLE

                        binding.placeListView.setOnItemClickListener { adapterView, view, i, l ->
                            if (type == "start") {
                                startPlaceData = placeDetail[i]
                                binding.placeListView.visibility = View.GONE
                            } else {
                                endPlaceData = placeDetail[i]
                                binding.placeListView.visibility = View.GONE
                            }
                        }

                    }catch (e : NullPointerException){
                        Log.d("에러", e.message.toString())
                    }
                }
                else {
                    Log.d("확인", "에러")
                }
            }

            override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                Log.d("에러", t.message.toString())
            }
        })
        Log.d("확인", "출발지 검색 작동")
    }

    private fun getPath() {
        val call = ApiObject2.retrofitService.getPAth(
            start_latLon = "37.5055196999994,126.94229594606628",
            start_name = "한국공학대",
            destination = "37.5055196999994,126.94229594606628",
            destination_name = "한국공학대")

        call.enqueue(object : retrofit2.Callback<Path> {
            override fun onResponse(call: Call<Path>, response: Response<Path>) {
                if (response.isSuccessful) {
                    try {
                        showToast(response.body().toString())
                    }catch (e : NullPointerException){
                    }
                }
            }

            override fun onFailure(call: Call<Path>, t: Throwable) {
                showToast(t.message.toString())
            }
        })

        //binding.adrId.text = places!![0].jibunAddress;
    }
}