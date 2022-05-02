package com.first.ridingpartnerinsiheung.scenarios.map
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.first.ridingpartnerinsiheung.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.GoogleMap

class MainActivity : AppCompatActivity() {
    private lateinit var mMap: GoogleMap    //  현재 위치를 검색하기 위해
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위치값 사용
    //FusedLocationProviderClient API 사용시 GPS 신호 및 와이파이와 통신사 네트워크 위치를 결합해 최소한의 배터리 사용량으로 빠르고 정확한 위치 검색
    private lateinit var locationCallback: LocationCallback //  위치값 요청에 대한 갱신 정보를 받아옴
    private var PERMISSION_ACCESS_CODE = 100

    lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 사용할 권한 array로 저장
        var permissions=arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        requirePermissions(permissions)

    }
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
        Toast.makeText(this, "위치 권한 설정!!", Toast.LENGTH_SHORT).show() // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }

    // 권한이 없는 경우 실행
    private fun permissionDenied(requestCode: Int) {
        Toast.makeText(this
            , "권한 승인이 필요합니다."
            , Toast.LENGTH_LONG)
            .show()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

}