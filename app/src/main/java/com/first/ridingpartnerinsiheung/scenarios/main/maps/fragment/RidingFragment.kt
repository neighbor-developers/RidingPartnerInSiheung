package com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentRidingBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.maps.CalDistance
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.first.ridingpartnerinsiheung.scenarios.main.maps.RidingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat

class RidingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding : FragmentRidingBinding
    private val viewModel by viewModels<RidingViewModel>()

    private lateinit var mMap : GoogleMap
    private var ridingState = false // 라이딩 상태

    private var curLatLng = LatLng(0.0,0.0) // 맵 켰을때 위치
    private var startLatLng = LatLng(0.0,0.0) // polyline 시작점
    private var endLatLng = LatLng(0.0,0.0) // polyline 끝점

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MyLocationCallBack
    private var mLocation : Location? = null
    private var myLocationMarker : Marker? = null

    private var savedTimer = 0
    private var savedSpeed = 0.0
    private var savedDistance = 0.0

    private var startTime = ""
    private var endTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding()

        initLocation()
        changeRidingState()


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.ridingMapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_riding, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
    private fun changeRidingState() {
        binding.startBtn.setOnClickListener {
            // 시작 눌렀을때
            binding.startBtn.visibility = View.GONE
            binding.pauseBtn.visibility = View.VISIBLE
            binding.saveAndStopBtn.visibility = View.GONE

            ridingState = true // 라이딩중
            viewModel.mLocation?.let {
                var startLatLng = LatLng(it.latitude, it.longitude)
                viewModel.befLat = startLatLng.latitude
                viewModel.befLon = startLatLng.longitude

                viewModel.calDisSpeed() // 속도, 거리, 주행시간 계산 시작
                startTime = viewModel.getTimeNow() // 시작 시잔

                var startMarker = marker(startLatLng, "출발지점") // 출발지점 마크
                myLocationMarker?.remove() // 현재위치 마크 삭제
            }
        }
        binding.pauseBtn.setOnClickListener {
            binding.startBtn.visibility = View.VISIBLE
            binding.startBtn.text = "다시 시작"
            binding.saveAndStopBtn.visibility = View.VISIBLE
            binding.pauseBtn.visibility = View.GONE
            ridingState = false

            mLocation?.let {
                viewModel.stopCal()

                savedSpeed = viewModel.averSpeed.value // 평균속도 받아오기
                savedTimer = viewModel.timer.value // 총 주행시간 받아오기
                savedDistance = viewModel.sumDistance.value // 총 주행거리 받아오기

                var endLatLng = LatLng(it.latitude, it.longitude)
                endTime = viewModel.getTimeNow()

                var endMarker = marker(endLatLng, "도착 지점")
            }
            // 다이얼로그 키면서 저장 및 중지하시겠습니까? -> 데이터 저장
        }
        binding.saveAndStopBtn.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("기록 저장")
            builder.setMessage("저장하시겠습니까?")
            builder.setPositiveButton("저장") { dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
            }

            builder.show()
        }
    }
     //시작지점 마크
    private fun marker(latLng: LatLng, title : String) : Marker? {
        val marker = mMap.addMarker(MarkerOptions().position(latLng).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        return marker
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }
    fun initLocation(){
        fusedLocationProviderClient = FusedLocationProviderClient(requireContext())
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        addLocationListener()
    }
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    inner class MyLocationCallBack: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val location = locationResult.lastLocation
            changeCurrentLocation(location)

            location.run {
                val latLng = LatLng(latitude, longitude)
                if(ridingState){
                    drawPath(latLng)
                }
            }
        }
    }

    private fun drawPath(latLng: LatLng){
        if(startLatLng == LatLng(0.0, 0.0)){
            startLatLng = latLng
        }
        endLatLng = LatLng(latLng.latitude, latLng.longitude)
        val polylineOptions = PolylineOptions().add(startLatLng).add(endLatLng).width(5f).color(Color.RED)
        mMap.addPolyline(polylineOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 18f));
        startLatLng = endLatLng
    }

    private fun changeCurrentLocation(location : Location){
        if (mLocation==null){
            curLatLng = LatLng(location.latitude, location.longitude)
            myLocationMarker = marker(curLatLng, "현재위치")
        }
        mLocation = location
        viewModel.mLocation = location
    }
    override fun onResume() {
        super.onResume()
        checkPermission()
    }
    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }
    fun removeLocationListener() { // 앱 동작하지 않을때에는 위치 정보 요청 제거
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }


    private val PERMISSION_CODE = 100

    //  권한 요청
    private fun requirePermissions(permissions: Array<String>){

        val isAllPermissionsGranted = permissions.all { //  permissions의 모든 권한 체크
            ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllPermissionsGranted) {    //  모든 권한이 허용되어 있을 경우
            Toast.makeText(requireContext(), "권한 있음", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(requireContext(), "위치 권한 필요", Toast.LENGTH_SHORT).show()
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