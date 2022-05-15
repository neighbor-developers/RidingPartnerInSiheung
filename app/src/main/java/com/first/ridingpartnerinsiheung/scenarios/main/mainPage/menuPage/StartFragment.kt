package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.menuPage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentStartBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage.MyPageFragment
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage.PathListFragment
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StartFragment : Fragment() {

    //viewBinding
    private val viewModel by viewModels<StartViewModel>()
    lateinit var binding : FragmentStartBinding

    private val PERMISSION_CODE = 100

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
        showToast("위치 권한 수락 완료") // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }
    // 권한이 없는 경우 실행
    private fun permissionDenied() {
        showToast("위치 권한이 필요합니다")
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

    private lateinit var mLastLocation : Location
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private lateinit var mLocationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        initClickListener()

        // 날씨 정보를 받아오기 위한 GPS 받기
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdate()
        //viewModel.changeLocation(mLastLocation.latitude, mLastLocation.longitude)
        viewModel.changeLocation(37.3425, 126.7502)

        //뷰모델 실행중 날씨모델 mutablestateflow 관찰해서 이미지 뷰 세팅
        viewModel.run{
            weather.onEach{
                when(it!!.sky){
                    "맑음" -> binding.skyTypeImg.setImageResource(R.drawable.sun)
                    "구름 많음" -> binding.skyTypeImg.setImageResource(R.drawable.cloud)
                    "흐림" -> binding.skyTypeImg.setImageResource(R.drawable.overcast)
                    else -> binding.skyTypeImg.setImageResource(R.drawable.sun)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        return binding.root
    }
    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initClickListener(){
        binding.recommendBtn.setOnClickListener {
            (activity as MainActivity).setFrag(PathListFragment())
        }
        binding.findOfficeBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
        binding.myPageBtn.setOnClickListener {
            (activity as MainActivity).setFrag(MyPageFragment())
        }
        binding.ridingBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
    }

    private fun startLocationUpdate(){
        checkPermission()

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
    }

    private var mLocationCallback = object  : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location){
        mLastLocation = location
    }

}