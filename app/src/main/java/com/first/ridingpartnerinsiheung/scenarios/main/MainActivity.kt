package com.first.ridingpartnerinsiheung.scenarios.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.ActivityMainBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    //viewBinding
    private val viewModel by viewModels<MainViewModel>()
    lateinit var binding : ActivityMainBinding

    private var PERMISSION_ACCESS_CODE = 100

    //  권한 요청
    private fun requirePermissions(permissions: Array<String>){

        val isAllPermissionsGranted = permissions.all { //  permissions의 모든 권한 체크
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        } //  모두 권한 허용될 경우 1
        if (isAllPermissionsGranted) {    //  모든 권한이 허용될 경우
            permissionGranted(PERMISSION_ACCESS_CODE)
        } else { //  그렇지 않을 경우 재시도도
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ACCESS_CODE)
        }
    }

    // 권한 요청 완료시 이 함수를 호출해 권한 요청에 대한 결과를 argument로 받을 수 있음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode === PERMISSION_ACCESS_CODE){
            if(grantResults.isNotEmpty()){
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied(requestCode)
                    }
                }
            }
        }
    }

    // 권한이 있는 경우 실행
    private fun permissionGranted(requestCode: Int) {
        showToast("위치 권한 설정!!") // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }

    // 권한이 없는 경우 실행
    private fun permissionDenied(requestCode: Int) {
        showToast("권한요청이 필요합니다")
    }

    // 날씨 구할 당일 날짜, 시간
    private var currentTime = System.currentTimeMillis()
    private var date = SimpleDateFormat("yyyyMMdd").format(currentTime)
    private var timeH = SimpleDateFormat("HH").format(currentTime)
    private var timeM = SimpleDateFormat("mm").format(currentTime)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 사용할 권한 array로 저장
        var permissions=arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        // 권한 확인 및 요헝
        requirePermissions(permissions)

        // 예비 위도 경도
        var lat = 37.3425
        var lon = 126.7502

        viewModel.setWeather(lat, lon, date, timeH, timeM)
    }
}