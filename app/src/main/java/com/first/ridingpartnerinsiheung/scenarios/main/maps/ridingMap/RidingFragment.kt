package com.first.ridingpartnerinsiheung.scenarios.main.maps.ridingMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.MySharedPreferences
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.FragmentRidingBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordActivity
import com.first.ridingpartnerinsiheung.views.dialog.RidingSaveDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode

import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.flow.collect

class RidingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mNaverMap: NaverMap

    private var ridingState = true // 라이딩 상태
    private var startLatLng = LatLng(0.0, 0.0) // polyline 시작점
    private var endLatLng = LatLng(0.0, 0.0) // polyline 끝점

    private lateinit var binding: FragmentRidingBinding
    private val viewModel by viewModels<RidingViewModel>()

    private lateinit var locationSource: FusedLocationSource

    private var locationManager: LocationManager? = null

    private var savedTimer = 0
    private var savedSpeed = 0.0
    private var savedDistance = 0.0
    private var savedKcal = 0.0

    private var startTime = ""
    private var endTime = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initBinding()
        initMapView()
        initObserve()

        return binding.root
    }
    private fun initMapView(){
        locationSource = FusedLocationSource(this, PERMISSION_CODE)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.ridingMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.ridingMapView, it).commit()
            }
        mapFragment?.getMapAsync(this)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_riding, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.event.collect { event ->
                when (event) {
                    RidingViewModel.RidingEvent.StartRiding -> startRiding()
                    RidingViewModel.RidingEvent.StopRiding -> stopRiding()
                    RidingViewModel.RidingEvent.SaveRiding -> saveRiding()
                    is RidingViewModel.RidingEvent.PostFailuer -> showToast("저장 실패")
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
        mNaverMap.locationTrackingMode = LocationTrackingMode.Face
        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.subIcon =
            OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_location_overlay_icon)
        locationOverlay.subIconWidth = 40
        locationOverlay.subIconHeight = 40
        locationOverlay.subAnchor = PointF(0.5f, 0.5f)
    }

    private fun changeLocation() {
        mNaverMap.addOnLocationChangeListener { location ->
            if (mNaverMap.locationTrackingMode == LocationTrackingMode.NoFollow) {
                setOverlay()
            }
            drawPath(LatLng(location.latitude, location.longitude))
            viewModel.mLocation.value = location
        }
    }
    private fun drawPath(latLng:LatLng) {
        endLatLng = LatLng(latLng.latitude, latLng.longitude)

        val path = PathOverlay()
        path.coords = listOf(startLatLng, endLatLng)
        path.color = Color.BLUE
        path.map = mNaverMap

        startLatLng = endLatLng
    }

    private fun startRiding(){ // 시작버튼
        binding.startBtn.visibility = View.GONE
        binding.stopBtn.visibility = View.VISIBLE
        binding.saveBtn.visibility = View.GONE
        startTime = viewModel.getTimeNow() // 시작 시감

        locationSource.lastLocation?.let { location ->
            startLatLng =
                LatLng(location.latitude, location.longitude)
            var startMarker = marker(startLatLng, "출발지점") // 출발지점 마크
        }
        changeLocation()
        setOverlay()

        if(ridingState) {
        // 라이딩중
            var startMarker = marker(startLatLng, "출발지점") // 출발지점 마크
        }else{
            var restartMarker = marker(startLatLng, "재출발지점") // 출발지점 마크
        }
        viewModel.befLatLng = startLatLng
        viewModel.calDisSpeed() // 속도, 거리, 주행시간 계산 시작
    }
    private fun stopRiding() { // 중지버튼
        ridingState = false
        binding.startBtn.visibility = View.VISIBLE
        binding.startBtn.text = "이어서 라이딩하기"
        binding.saveBtn.visibility = View.VISIBLE
        binding.stopBtn.visibility = View.GONE

        endTime = viewModel.getTimeNow()

        locationSource.lastLocation?.let { location ->
            viewModel.stopCal()

            endLatLng = LatLng(location.latitude, location.longitude)
            var endMarker = marker(endLatLng, "도착 지점")
        }
    }
    private fun saveRiding(){
        savedSpeed = viewModel.averSpeed.value // 평균속도 받아오기
        savedTimer = viewModel.timer.value // 총 주행시간 받아오기
        savedDistance = viewModel.sumDistance.value // 총 주행거리 받아오기
        savedKcal = viewModel.calculateKcal(savedSpeed, savedTimer, 60.0)  //  몸무게 임의로 60 설정

        val data = RidingData(savedDistance, savedSpeed, savedTimer, savedKcal)
        // 페이지 이동
        val dialog = RidingSaveDialog(requireContext())
        dialog.start()
        dialog.setOnClickListener(object: RidingSaveDialog.DialogOKCLickListener{
            override fun onOKClicked() {
                viewModel.saveData(onFailure = { showToast("저장 실패") }, data, endTime)
                var prefs = MySharedPreferences((activity as MapActivity).applicationContext)
                if (prefs.ridingDateList.isNullOrEmpty()) {
                    prefs.ridingDateList = endTime
                } else {
                    prefs.ridingDateList = endTime + "," + prefs.ridingDateList
                }
                val intent = Intent(requireContext(), RecordActivity::class.java)
                intent.putExtra("time",endTime)
                intent.putExtra("data", data)
                startActivity(intent)

            }
        })
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

    override fun onResume() {
        super.onResume()
        requirePermissions()
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