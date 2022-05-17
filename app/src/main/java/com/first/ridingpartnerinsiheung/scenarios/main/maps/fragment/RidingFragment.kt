package com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment

import android.Manifest
import android.annotation.SuppressLint
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
import com.first.ridingpartnerinsiheung.scenarios.main.maps.CalDistance
import com.first.ridingpartnerinsiheung.scenarios.main.maps.RidingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.text.SimpleDateFormat

class RidingFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding : FragmentRidingBinding
    private val viewModel by viewModels<RidingViewModel>()

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: RidingFragment.MyLocationCallBack
    var mLocation : Location? = null

    private lateinit var mMap : GoogleMap
    private var ridingState = false // 라이딩 상태

    var startTime = ""
    var endTime = ""

    var startLatLng = LatLng(0.0,0.0) // polyline 시작점
    var endLatLng = LatLng(0.0,0.0) // polyline 끝점


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
    private fun changeRidingState(){
        binding.changeRidingStateBtn.setOnClickListener {
            if(!ridingState) {
                binding.changeRidingStateBtn.text = "중지"
                ridingState = true
                viewModel.mLocation?.let {
                    startLatLng = LatLng(it.latitude, it.longitude)
                    marker(startLatLng)
                    startTime = viewModel.currentTime.toString()
                    viewModel.getDisSpeed()
                }
            }else{
                binding.changeRidingStateBtn.text = "시작"
                viewModel.mLocation?.let {
                    endLatLng = LatLng(it.latitude, it.longitude)
                    marker(endLatLng)
                    //viewModel.endTime = getTimeNow()
                    viewModel.timeHandler.removeMessages(0)
                    endTime = viewModel.currentTime.toString()
                }
            }
        }
    }
    private fun marker(latLng: LatLng){
        mMap.addMarker(MarkerOptions().alpha(0.5f).anchor(0.5f, 0.5f).position(latLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val marker = LatLng(35.241615, 128.695587)
        mMap.addMarker(MarkerOptions().position(marker).title("Marker LAB"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    private fun initLocation(){
        fusedLocationProviderClient = FusedLocationProviderClient(requireContext())
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                while(ridingState){
                    endLatLng = LatLng(latitude, longitude)
                    mMap.addPolyline(polylineOptions)
                    startLatLng = LatLng(latitude, longitude)
                }
            }

        }
    }
    private val polylineOptions = PolylineOptions()
//        .add(
//        when(startLatLng){
//            LatLng(0.0,0.0) -> LatLng(mLocation!!.latitude, mLocation!!.longitude)
//            else -> startLatLng
//        })
        .add(startLatLng).add(endLatLng).width(5f).color(Color.RED)

    private fun changeCurrentLocation(location : Location){
        mLocation = location
        viewModel.mLocation = mLocation

        viewModel.befLat = location.latitude
        viewModel.befLon = location.longitude

        val marker = LatLng(mLocation!!.latitude, mLocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(marker).title("내 위치"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))

    }
    override fun onResume() {
        super.onResume()
        checkPermission()
    }
    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }
    private fun removeLocationListener() { // 앱 동작하지 않을때에는 위치 정보 요청 제거
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    private val PERMISSION_CODE = 1313

    //  권한 요청
    private fun requirePermissions(permissions: Array<String>){

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
        addLocationListener()
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