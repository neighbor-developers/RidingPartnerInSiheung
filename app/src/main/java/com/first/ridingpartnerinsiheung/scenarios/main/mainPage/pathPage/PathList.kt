package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.pathPage

import android.content.Context
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.PlaceList

class PathList(private val context: Context) {
    fun read():List<PlaceList>{
        return listOf(
            PlaceList("오이도역 - 오이도 - 반달섬", "30km", R.drawable.place_oido,R.drawable.img_route_coastline ,coastline),
            PlaceList("그린웨이", "20km", R.drawable.place_mulwang, R.drawable.img_route_greenway, greenWay),
            PlaceList("오이도역 - 시화방조제 - 대부도 공원", "22km", R.drawable.place_sihwa,R.drawable.img_route_sihwa_seawall, sihwaSeawall),
            PlaceList("시흥 순환 코스", "24km", R.drawable.place_botong, R.drawable.img_route_coastline,siheungCycleCourse), // 오류로 사진 대체
            PlaceList("한국공학대 - 정왕역 코스", "10km", R.drawable.place_kpu, R.drawable.img_route_commuting_kpu, commutingKpu)
        )
    }

    // 정왕역
    private val jeongWangStation = "126.7433065,37.3516988,placeid=13479469,name=정왕역 4호선"
    // 한국공학대
    private val kpu = "126.7335061,37.3400342,placeid=11591658,name=한국공학대학교"
    // 오이도역
    private val oidoStation = "126.7388047,37.3626509,placeid=21805865,name=오이도역 수인분당선"
    // 오이도 빨강 등대
    private val oidoRedLighthouse = "126.6875524,37.3456389,placeid=1869342267,name=오이도빨간등대"
    // 연꽃테마공원
    private val lotusFlowerThemePark = "126.8072473,37.4023275,placeid=13143571,name=연꽃테마파크"
    // 물왕저수지
    private val waterKing = "126.8343379,37.3823899,placeid=13491058,name=물왕호수"
    // 옥구공원
    private val okguPark = "126.7120024,37.3555974,placeid=11622429,name=옥구공원"
    // 월곶항
    private val wolgotPort = "126.7383671,37.3869561,placeid=13491554,name=월곶포구"
    // 갯골생태공원
    private val gaetgolEcologyPark = "126.7811118,37.3909834,placeid=12958532,name=갯골생태공원"
    // 배곧생명공원
    private val baegotSaengmyeongPark = "126.7217521,37.3720949,placeid=37310952,name=배곧생명공원"
    // 거북섬
    private val tutleIsland = "127.0606608,37.0649627,placeid=13270685,name=거북섬"
    // 반달섬
    private val halfMoonIsland = "126.7381704,37.3000201,placeid=35795149,name=반달섬1로"
    // 시화 방조제
    private val seawall = "126.6063404,37.3093672,placeid=19029247,name=시화방조제"
    // 대부도 공원
    private val daebudoPark = "126.5792887,37.2902425,placeid=19029571,name=대부도공원"
    // 고잔역
    private val gojanStation = "126.8234083,37.3168245,placeid=13479320,name=고잔역 4호선"
    // 화정천
    private val hwajeongcheon = "126.8209368,37.3119297,placeid=19009887,name=화정천"
    // 소래습지생태공원
    private val soraePark = "126.7475643,37.4134966,placeid=36480728,name=소래습지생태공원"

    // 경로들
    private val commutingKpu = arrayListOf(jeongWangStation, kpu)
    private val greenWay = arrayListOf(waterKing, lotusFlowerThemePark, gaetgolEcologyPark)
    private val coastline = arrayListOf(oidoStation, baegotSaengmyeongPark, okguPark, oidoRedLighthouse, tutleIsland, halfMoonIsland)
    private val sihwaSeawall = arrayListOf(oidoStation, baegotSaengmyeongPark, okguPark, oidoRedLighthouse, seawall, daebudoPark);
    private val siheungCycleCourse = arrayListOf(gojanStation, hwajeongcheon, halfMoonIsland, tutleIsland, oidoRedLighthouse, soraePark, waterKing, gojanStation);
}