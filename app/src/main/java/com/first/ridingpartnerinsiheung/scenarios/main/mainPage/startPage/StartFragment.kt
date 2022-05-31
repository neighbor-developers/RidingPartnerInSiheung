package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.startPage

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import com.first.ridingpartnerinsiheung.data.MySharedPreferences
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.FragmentStartBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StartFragment : Fragment() {

    //viewBinding
    private val viewModel by viewModels<StartViewModel>()
    lateinit var binding : FragmentStartBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    var recordArray = arrayListOf<String>()
    private lateinit var mLastLocation : Location
    private var mFusedLocationProviderClient : FusedLocationProviderClient? = null
    private lateinit var mLocationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()

        // 날씨 정보를 받아오기 위한 GPS 받기
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdate()


        //뷰모델 실행중 날씨모델 mutablestateflow 관찰해서 이미지 뷰 세팅
        viewModel.run{
            weather.onEach{
                when(it!!.sky){
                    "맑음" -> binding.skyTypeImg.setImageResource(R.drawable.icon_whether_sun3)
                    "구름 많음" -> binding.skyTypeImg.setImageResource(R.drawable.icon_whether_cloud)
                    "흐림" -> binding.skyTypeImg.setImageResource(R.drawable.icon_whether_overcast)
                    else -> binding.skyTypeImg.setImageResource(R.drawable.icon_whether_sun3)
                }
                when(it!!.rainType){
                    "강수 예정 없음" ->binding.rainTypeImg.setImageResource(R.drawable.icon_whether_sun)
                    "비" -> binding.rainTypeImg.setImageResource(R.drawable.icon_whether_umbrella)
                    "비/눈" -> binding.rainTypeImg.setImageResource(R.drawable.icon_whether_umbrella)
                    "눈" ->  binding.rainTypeImg.setImageResource(R.drawable.icon_whether_snow)
                    "빗방울" -> binding.rainTypeImg.setImageResource(R.drawable.icon_whether_umbrella)
                    "빗방울 눈날림"-> binding.rainTypeImg.setImageResource(R.drawable.icon_whether_umbrella)
                    "눈날림" -> binding.rainTypeImg.setImageResource(R.drawable.icon_whether_snow)
                }
//
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }


// TODO : doc 가 null 이라고 나오는데 파이어베이스에 진짜 없는건지 아이디가 일치안해서 없는건지 확인하고 진짜 없는거면 수정 해야함

//        주석 해제하면 오류날수 있습니다!
        var prefs = MySharedPreferences((activity as MainActivity).applicationContext)
        var recentRecord : RidingData
        if (prefs.recentRidingTime!=""){
            showToast("데이터 불러오는 중")
            val recDoc = db.collection(user)
                .document(prefs.recentRidingTime!!)

            recDoc.get().addOnSuccessListener { documentSnapshot ->
                recentRecord = documentSnapshot.toObject<RidingData>()!!
                binding.recentRidingTv.text = recentRecord.sumDistance.toString()
            }

        }




        return binding.root
    }



    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate(){
        requirePermissions()

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
    }

    private var mLocationCallback = object  : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            mLastLocation = locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location){
        mLastLocation = location
        viewModel.changeLocation(mLastLocation.latitude, mLastLocation.longitude)
    }
    private val PERMISSION_CODE = 100

    //  권한 요청
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