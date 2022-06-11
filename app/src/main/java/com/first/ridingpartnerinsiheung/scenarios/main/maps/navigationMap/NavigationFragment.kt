package com.first.ridingpartnerinsiheung.scenarios.main.maps.navigationMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
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
import com.first.ridingpartnerinsiheung.databinding.FragmentNavigationBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.naver.maps.geometry.LatLng
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

        getPath(startParam, destinationParam) {s -> drawPath(s)}

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
//                    NavigationViewModel.NavigationEvent.SaveNavigation -> saveNavigation()
                    is NavigationViewModel.NavigationEvent.PostFailuer -> showToast("저장 실패")
                }
            }
        }
    }

    private fun startNavigation(){ // 시작버튼
        binding.startBtn.visibility = View.GONE
        binding.stopBtn.visibility = View.VISIBLE
        binding.saveBtn.visibility = View.GONE

        var startTime = viewModel.getTimeNow()

        changeLocation()
        setOverlay()
    }

    private fun stopNavigation() { // 중지버튼
        binding.startBtn.visibility = View.VISIBLE
        binding.startBtn.text = "이어서 라이딩하기"
        binding.saveBtn.visibility = View.VISIBLE
        binding.stopBtn.visibility = View.GONE

        var endTime = viewModel.getTimeNow()
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
        return marker
    }

    private fun getPath(start: String, destination: String, onPath: (Path.RouteSummary) -> Unit) {
        val call = ApiObject2.retrofitService.getPath(
            start, // "126.9820673,37.4853855,name=이수역 7호선",
            destination, // "126.9803409,37.5029146,name=동작역 4호선",
        )

        call.enqueue(object : retrofit2.Callback<Path> {
            override fun onResponse(call: Call<Path>, response: Response<Path>) {
                if (response.isSuccessful) {
                    try {
                        guides = response.body()!!.routes[0].legs[0].steps.map { step ->
                            step.guide
                        }
                        onPath(response.body()!!.routes[0].summary)
                    } catch (e: NullPointerException) {
                    }
                } else {
                    Log.d("에러", "ㅜㅜ1")
                }
            }

            override fun onFailure(call: Call<Path>, t: Throwable) {
                Log.d("에러", "ㅜㅜ2")
            }
        })
    }

    private fun drawPath(summary: Path.RouteSummary) {
        val startLatLng = LatLng(
            summary.start.location.split(",")[0].toDouble(),
            summary.start.location.split(",")[1].toDouble()
        )

        val waypoints = summary.road_summary.map {
            LatLng(
                it.location.split(",")[0].toDouble(),
                it.location.split(",")[1].toDouble()
            )
        }

        val endLatLng = summary.end.location.split(",").let {
            LatLng(it[0].toDouble(), it[1].toDouble())
        }

        marker(startLatLng, "출발지")
        marker(endLatLng, "도착지")
        val fullPath: List<LatLng> = listOf(startLatLng) + waypoints + endLatLng

        path.coords = fullPath
        path.color = Color.BLUE
        path.map = mNaverMap
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
