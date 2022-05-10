package com.first.ridingpartnerinsiheung.scenarios.main.maps.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(),OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    private lateinit var mMap: GoogleMap
    private lateinit var mView: MapView

    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치값 사용
    private lateinit var locationCallback: LocationCallback //  위치값 요청에 대한 갱신 정보를 받아옴
    private lateinit var locationRequest: LocationRequest

    private val myLocation = LatLng(37.3156, 126.804)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        val mapView = inflater.inflate(R.layout.fragment_map, container, false)

        mView = mapView.findViewById(R.id.map)
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this) // 구글맵을 불러오는 함수, this->MapsFragment->OnMapReadyCallback->onMapready()로 들어감

        return mapView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(requireContext())
        updateLocation()

        val markerCircle = CircleOptions() // circle로 현재 위치 표시
        markerCircle.center(myLocation) //  circle의 중심
        mMap.addCircle(markerCircle)    //  지도에 circle 표시
        // mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
    }
    private fun updateLocation() {
        locationRequest = LocationRequest.create() // 위치 정보 요청

        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() { //  위치 정보 갱신
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.let {
                    for (location in it.locations) {
                        Log.d("Location", "${location.latitude} , ${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    fun setLastLocation(lastLocation: Location) {
        val myLocation = LatLng(lastLocation.latitude, lastLocation.longitude)

        var markerCircle = CircleOptions() // circle로 현재 위치 표시
        markerCircle.center(myLocation) //  circle의 중심

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))


        val cameraPosition = CameraPosition.Builder()
            .target(myLocation)
            .zoom(15.0f)
            .build()
        mMap.clear()
        mMap.addCircle(markerCircle)    //  지도에 circle 표시
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)) // 카메라 이동
    }
}