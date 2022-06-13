package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.api.bikepath.ApiObject2
import com.first.ridingpartnerinsiheung.api.bikepath.Path
import com.first.ridingpartnerinsiheung.data.MySharedPreferences
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.FragmentNavigationBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.maps.MapActivity
import com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordActivity
import com.first.ridingpartnerinsiheung.views.dialog.RidingSaveDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Response

class NavigationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNavigationBinding
    private val viewModel = NavigationViewModel()

    private lateinit var mNaverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var locationManager: LocationManager? = null
    private lateinit var path: PathOverlay
    private lateinit var guides: List<Path.Guide>

    // 라이딩 프래그먼트에서 가져온 변수 추후 수정예정
    private var ridingState = true // 라이딩 상태
    private var ridingStartLatLng = LatLng(0.0, 0.0) // polyline 시작점
    private var ridingEndLatLng = LatLng(0.0, 0.0) // polyline 끝점

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
        initObserves()
        initMapView()

        val startParam = arguments?.getString("startParam").toString()
        val destinationParam = arguments?.getString("destinationParam").toString()
        val wayPointParam = arguments?.getString("wayPointParam").toString()

        Handler().postDelayed({
            getPath(startParam, destinationParam, wayPointParam) {route -> drawPath(route)}
        }, 500)

        return binding.root
    }

    private fun initMapView() {
        locationSource = FusedLocationSource(this, PERMISSION_CODE)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.navigationMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.navigationMapView, it).commit()
            }
        mapFragment?.getMapAsync(this)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initObserves() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.navigationEvent.collect() { event ->
                when (event) {
                    NavigationViewModel.NavigationEvent.StartNavigation -> startNavigation()
                    NavigationViewModel.NavigationEvent.StopNavigation -> stopNavigation()
                    NavigationViewModel.NavigationEvent.SaveNavigation -> saveNavigation()
                    is NavigationViewModel.NavigationEvent.PostFailuer -> showToast("저장 실패")
                }
            }
        }
    }

    private fun saveNavigation(){
        savedSpeed = viewModel.averSpeed.value // 평균속도 받아오기
        savedTimer = viewModel.timer.value // 총 주행시간 받아오기
        savedDistance = viewModel.sumDistance.value // 총 주행거리 받아오기
        savedKcal = viewModel.calcModel.calculateKcal(savedSpeed, savedTimer, 60.0)  //  몸무게 임의로 60 설정

        val data = RidingData(savedDistance, savedSpeed, savedTimer, savedKcal)
        // 페이지 이동
        val dialog = RidingSaveDialog(requireContext())
        dialog.start()
        dialog.setOnClickListener(object: RidingSaveDialog.DialogOKCLickListener{
            override fun onOKClicked() {
                viewModel.saveData(onFailure = { showToast("저장 실패") }, data, endTime)
                val prefs = MySharedPreferences((activity as MapActivity).applicationContext)
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


    private fun startNavigation(){ // 시작버튼
        binding.startBtn.visibility = View.GONE
        binding.stopBtn.visibility = View.VISIBLE
        binding.saveBtn.visibility = View.GONE
        binding.navigationLayout.visibility = View.VISIBLE

        var startTime = viewModel.getTimeNow()

        locationSource.lastLocation?.let { location ->
            ridingStartLatLng =
                LatLng(location.latitude, location.longitude)
            marker(ridingStartLatLng, "라이딩 출발 지점") // 출발지점 마크
        }

        changeLocation()
        setOverlay()

        viewModel.befLatLng = ridingStartLatLng
        viewModel.calDisSpeed() // 속도, 거리, 주행시간 계산 시작
    }

    private fun stopNavigation() { // 중지버튼
        binding.startBtn.visibility = View.VISIBLE
        binding.startBtn.text = "이어서 라이딩하기"
        binding.saveBtn.visibility = View.VISIBLE
        binding.stopBtn.visibility = View.GONE

        endTime = viewModel.getTimeNow()

        locationSource.lastLocation?.let { location ->
            viewModel.stopCal()

            ridingEndLatLng = LatLng(location.latitude, location.longitude)
            marker(ridingEndLatLng, "라이딩 종료 지점")
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.locationSource = locationSource
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
        path = PathOverlay()

        setOverlay()
    }

    private fun setOverlay() {
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.subIcon =
            OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_location_overlay_icon)
        locationOverlay.subIconWidth = 40
        locationOverlay.subIconHeight = 40
        locationOverlay.subAnchor = PointF(0.5f, 0.5f)
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
        marker.height = 70
        marker.captionText = title
        if(title == "도착지"){
            marker.icon = MarkerIcons.RED
        }
        if(title == "라이딩 출발 지점"){
            marker.icon = MarkerIcons.LIGHTBLUE
        }
        if(title == "라이딩 종료 지점"){
            marker.icon = MarkerIcons.YELLOW
        }
        if(title == "경유지"){
            marker.icon = MarkerIcons.GRAY
        }
        return marker
    }

    private fun getPath(start: String, destination: String, wayPoint: String, onPath: (Path.Route) -> Unit) {
        val call = ApiObject2.retrofitService.getPath(
            start, // "126.9820673,37.4853855,name=이수역 7호선",
            destination, // "126.9803409,37.5029146,name=동작역 4호선",
            wayPoint,
        )

        call.enqueue(object : retrofit2.Callback<Path> {
            override fun onResponse(call: Call<Path>, response: Response<Path>) {
                if (response.isSuccessful) {
                    try {
                        var routes = response.body()!!.routes[0]
                        guides = routes.legs.map { leg ->
                            leg.steps.map { step ->
                                step.guide
                            }
                        }.flatten()
                        onPath(routes)
                    } catch (e: NullPointerException) {
                        Log.d("에러", e.message.toString())
                    }
                } else {
                    Log.d("에러", "원인모를 통신 실패")
                }
            }

            override fun onFailure(call: Call<Path>, t: Throwable) {
                Log.d("에러", t.message.toString())
            }
        })
    }

    private fun drawPath(route: Path.Route) {
        var summary = route.summary
        var legs = route.legs

        val startLatLng = stringToLatLng(summary.start.location)

        val waypoints = legs.map { leg ->
            leg.steps.map { step ->
                if (step.path.isNullOrEmpty()) {
                    listOf()
                } else {
                    step.path.split(" ").map {
                        stringToLatLng(it)
                    }
                }
        }}.flatten().flatten()

        val endLatLng = stringToLatLng(summary.end.location)

        marker(startLatLng, "출발지")
        marker(endLatLng, "도착지")

        summary.waypoints?.forEach {
            var wayPointLatLng = stringToLatLng(it.location)
            marker(wayPointLatLng, "경유지")
        }
        // val fullPath: List<LatLng> = listOf(startLatLng) + waypoints + endLatLng

        // 네이버 맵 범위지정 함수 하지만 작동이 안된다 ㅠ
        mNaverMap.extent = LatLngBounds(
            LatLng(
                summary.bounds.right_bottom.split(",")[1].toDouble(),
                summary.bounds.left_top.split(",")[0].toDouble(),
            ),
            LatLng(
                summary.bounds.left_top.split(",")[1].toDouble(),
                summary.bounds.right_bottom.split(",")[0].toDouble(),
            ),
        )

        // 임시방편이라도 써야겠다 ㅠ
        val centerLatLng = LatLng(
            (summary.bounds.right_bottom.split(",")[1].toDouble() + summary.bounds.left_top.split(",")[1].toDouble()) / 2,
            (summary.bounds.left_top.split(",")[0].toDouble() + summary.bounds.right_bottom.split(",")[0].toDouble()) / 2
        )

        // 위치에 따른 줌레벨 조정 함수 필요 노가다 요망
        // moveCameraToCenter(right_bottom: String, left_top: String)

        // 임시 중앙 카메라 이동후 줌고정
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(centerLatLng, 12.0).animate(
            CameraAnimation.Easing)
        mNaverMap.moveCamera(cameraUpdate)

        path.coords = waypoints
        path.color = Color.BLUE
        path.width = 12
        path.patternImage = OverlayImage.fromResource(R.drawable.icon_navigator)
        path.patternInterval = 10
        path.map = mNaverMap
    }

    // 위치에 따른 줌레벨 조정 함수 노가다 요망 미완성
    private fun moveCameraToCenter(right_bottom: String, left_top: String) {
        val right = right_bottom.split(",")[0].toDouble();
        val bottom = right_bottom.split(",")[1].toDouble();
        val left = left_top.split(",")[0].toDouble();
        val top = left_top.split(",")[1].toDouble();

        val centerLatLng = LatLng(
            (top + bottom) / 2,
            (left + right) / 2
        )

        var zoomLevel: Double = 1.0

        val topBottomDiff = top - bottom
        val eastWestDiff = right - left

        // 시간나면 만들자....
        if (topBottomDiff > 10 || eastWestDiff > 10)
        {
            zoomLevel = 2.0
        }
        if (topBottomDiff > 20 || eastWestDiff > 20)
        {
            zoomLevel = 3.0
        }

        // 위치에 따른 줌레벨 조정 함수 필요 노가다 요망
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(centerLatLng, zoomLevel).animate(
            CameraAnimation.Easing)
        mNaverMap.moveCamera(cameraUpdate)
    }

    private fun stringToLatLng (str: String): LatLng {
        return LatLng(
            str.split(",")[1].toDouble(),
            str.split(",")[0].toDouble(),
        )
    }

    private fun changeLocation() {
        var nowLatLng: LatLng?
        var count = 0
        var nowDestination = guides[0]
        var nowDestinationLatLng = LatLng(
            nowDestination.turn_point.split(",")[0].toDouble(),
            nowDestination.turn_point.split(",")[1].toDouble()
        )

        binding.navigationPoint.text = nowDestination.point
        binding.navigationContent.text = nowDestination.instructions

        mNaverMap.addOnLocationChangeListener { location ->
            if (mNaverMap.locationTrackingMode == LocationTrackingMode.NoFollow) {
                setOverlay()
            }
            // 지나온길 표시
            path.passedColor = Color.GRAY

            // 현재 위치
            nowLatLng = LatLng(location.latitude, location.longitude)

            // 목표위치 도달할시
            if (nowDestinationLatLng.distanceTo(nowLatLng!!) < 10 ) {
                if (guides.size - 1 == count) {
                    finishNavigation()
                }

                // 목표지 갱신
                count++
                nowDestination = guides[count]
                nowDestinationLatLng = LatLng(
                    nowDestination.turn_point.split(",")[0].toDouble(),
                    nowDestination.turn_point.split(",")[1].toDouble()
                )

                binding.navigationPoint.text = nowDestination.point
                binding.navigationContent.text = nowDestination.instructions

                if (nowDestination.instructions.contains("좌회전")) {
                    binding.navigationImage.setImageResource(R.drawable.icon_insturctor_turn_left)
                } else if (nowDestination.instructions.contains("우회전")) {
                    binding.navigationImage.setImageResource(R.drawable.icon_insturctor_turn_right)
                } else if (nowDestination.instructions.contains("유턴")) {
                    binding.navigationImage.setImageResource(R.drawable.icon_insturctor_uturn)
                } else {
                    binding.navigationImage.setImageResource(R.drawable.icon_insturctor_straight)
                }
            }

            var lastLocation = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(lastLocation, 17.0).animate(
                CameraAnimation.Easing)
            mNaverMap.moveCamera(cameraUpdate)

            viewModel.mLocation.value = location
        }
    }

    private fun finishNavigation() {
        return
    }

    //  권한 요청
    private val PERMISSION_CODE = 100

    private fun requirePermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        val isAllPermissionsGranted = permissions.all { //  permissions의 모든 권한 체크
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
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

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                for (grant in grantResults) {
                    if (grant == PackageManager.PERMISSION_GRANTED) {
                        /*no-op*/
                    } else {
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
