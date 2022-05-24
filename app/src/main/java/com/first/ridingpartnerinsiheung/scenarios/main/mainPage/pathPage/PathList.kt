package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage

import android.content.Context
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.PlaceList
import com.google.android.gms.maps.model.LatLng

class PathList(private val context: Context) {
    fun read():List<PlaceList>{
        return listOf(
            PlaceList("오이도역 - 오이도 - 반달섬", "23.7km", R.drawable.oido, coastline),
            PlaceList("그린웨이", "23.7km", R.drawable.mulwang, greenWay),
            PlaceList("오이도역 - 시화방조제 - 대부도 공원", "23.7km", R.drawable.sihwa, sihwaSeawall),
            PlaceList("시흥 순환 코스", "23.7km", R.drawable.botong, siheungCycleCourse)
        )
    }

    // 오이도역
    private val oidoStation = LatLng(37.361404, 126.736222);
    // 오이도 빨강 등대
    private val oidoRedLighthouse = LatLng(37.345250, 126.687578);
    // 연꽃테마공원
    private val lotusFlowerThemePark = LatLng(37.403995, 126.806538)
    // 물왕저수지
    private val waterKing = LatLng(37.376673, 126.846598)
    // 옥구공원
    private val okguPark = LatLng(37.351663, 126.704559)
    // 월곶항
    private val wolgotPort = arrayOf(LatLng(37.343991285297, 126.74729588817))
    // 갯골생태공원
    private val gaetgolEcologyPark = LatLng(37.388889, 126.780391)
    // 배곧생명공원
    private val baegotSaengmyeongPark = LatLng(37.371382, 126.722070)
    // 거북섬
    private val tutleIsland = LatLng(37.324567, 126.675536)
    // 반달섬
    private val halfMoonIsland = LatLng(37.303498, 126.735512)
    // 시화 방조제
    private val seawall = LatLng(37.312324, 126.609361)
    // 대부도 공원
    private val daebudoPark = LatLng(37.290772, 126.578727)
    // 고잔역
    private val gojanStation = LatLng(37.290772, 126.578727)
    // 화정천
    private val hwajeongcheon = LatLng(37.290772, 126.578727)
    // 소래습지생태공원
    private val soraePark = LatLng(37.290772, 126.578727)


    // 경로들
    private val greenWay = arrayOf(waterKing, lotusFlowerThemePark, gaetgolEcologyPark)
    private val coastline = arrayOf(oidoStation, baegotSaengmyeongPark, okguPark, oidoRedLighthouse, tutleIsland, halfMoonIsland)
    private val sihwaSeawall = arrayOf(oidoStation, baegotSaengmyeongPark, okguPark, oidoRedLighthouse, seawall, daebudoPark);
    private val siheungCycleCourse = arrayOf(gojanStation, hwajeongcheon, halfMoonIsland, tutleIsland, oidoRedLighthouse, soraePark, waterKing, gojanStation);
}