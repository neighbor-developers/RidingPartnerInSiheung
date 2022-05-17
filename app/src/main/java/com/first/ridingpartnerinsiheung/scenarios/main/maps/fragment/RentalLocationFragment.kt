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

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RentalLocationFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rental_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap){

        val latLng = LatLng(37.349741467772, 126.76182486561)

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(12f))

        addMarkers(googleMap)
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true)
        } else{
            Toast.makeText(requireActivity(), "권한을 설정하세요", Toast.LENGTH_SHORT).show()
            permissionDenied()
            googleMap.setMyLocationEnabled(true)
        }
    }
    private val places:List<RentalLocation> by lazy{
        RentalLocationXY(requireActivity()).read()
    }

    private fun addMarkers(googleMap: GoogleMap){
        places.forEach{ place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.location)
            )
        }
    }
    private val PERMISSION_CODE = 9999

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
