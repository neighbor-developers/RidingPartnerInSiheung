package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.startPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ridingpartnerinsiheung.data.Date
import com.first.ridingpartnerinsiheung.data.LocationXY
import com.first.ridingpartnerinsiheung.api.weather.ApiObject
import com.first.ridingpartnerinsiheung.data.ModelWeather
import com.first.ridingpartnerinsiheung.api.weather.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.NullPointerException
import java.text.SimpleDateFormat

class StartViewModel() : ViewModel() {

    // 날씨 정보 담을 변수
    var weather = MutableStateFlow<ModelWeather?>(ModelWeather())

    // 날짜 시간 설정
    private val _date = MutableStateFlow(
        System.currentTimeMillis().let { current ->
            Date(
                SimpleDateFormat("yyyyMMdd").format(current),
                SimpleDateFormat("HH").format(current),
                SimpleDateFormat("mm").format(current)
            )
        }
    )
    private val date = _date.asStateFlow()

    // 임시 위치
    var nx = MutableStateFlow("56")
    var ny = MutableStateFlow("122")

    // 날씨 정보 받기
    init{
        viewModelScope.launch {
            var time = getTime(date.value.timeH, date.value.timeM)

            val call = ApiObject.retrofitService.getWeather(
                num_of_rows = 60,
                page_no = 2,
                data_type = "JSON",
                base_date = date.value.date,
                base_time = time,
                nx = nx.value,
                ny = ny.value)

            call.enqueue(object : retrofit2.Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful) {
                        try {
                            val _weather = ModelWeather()

                            val weatherData: ArrayList<Weather.Item> = response.body()!!.response.body.items.item
                            val total = response.body()!!.response.body.totalCount

                            for (i in 0 until total step 6) {
                                when (weatherData[i].category) {
                                    "PTY" -> _weather.rainType = weatherData[i].fcstValue // 강수형태
                                    "REH" -> _weather.humidity = weatherData[i].fcstValue // 습도
                                    "SKY" -> _weather.sky = weatherData[i].fcstValue // 하늘상태
                                    "T1H" -> _weather.temp = weatherData[i].fcstValue // 기온
                                }
                            }
                            _weather.rainType = getRainType(_weather.rainType)
                            _weather.sky = getSky(_weather.sky)
                            _weather.temp = _weather.temp+" °C"

                            weather.value = _weather
                        }catch (e : NullPointerException){
                        }
                    }
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    /**/
                }
            })

        }

    }
    // 강수 타입 텍스트 변환
    fun getRainType(rainType: String): String {
        return when (rainType) {
            "0" -> "강수 예정 없음"
            "1" -> "비"
            "2" -> "비/눈"
            "3" -> "눈"
            "5" -> "빗방울"
            "6" -> "빗방울 눈날림"
            "7" -> "눈날림"
            else -> "오류 rainType" + rainType
        }
    }
    // 날씨 상태 텍스트 변환
    fun getSky(sky: String): String {
        return when (sky) {
            "1" -> "맑음"
            "3" -> "구름 많음"
            "4" -> "흐림"
            else -> "오류 sky : " + sky
        }
    }
    fun changeLocation(lat:Double, lon:Double){
        val location = dfs_xy_conv(lat, lon)
        nx.value = location.x.toString()
        ny.value = location.y.toString()
    }
    // API에서 정보 받아올 수 있는 시간 형식으로 바꾸기
    private fun getTime(h :String, m : String): String{
        var result = ""

        result = if (m.toInt() < 45) {
            // 0시면 2330
            if (h == "00") "2330"
            // 아니면 1시간 전 날씨 정보 부르기
            else {
                var resultH = h.toInt() - 1
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10) "0" + resultH + "30"
                // 2자리면 그대로
                else resultH.toString() + "30"
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else h + "30"

        return result
    }
    // 위도 경도 격자 XY로 바꾸기
    fun dfs_xy_conv(v1: Double, v2: Double) : LocationXY {
        val RE = 6371.00877     // 지구 반경(km)
        val GRID = 5.0          // 격자 간격(km)
        val SLAT1 = 30.0        // 투영 위도1(degree)
        val SLAT2 = 60.0        // 투영 위도2(degree)
        val OLON = 126.0        // 기준점 경도(degree)
        val OLAT = 38.0         // 기준점 위도(degree)
        val XO = 43             // 기준점 X좌표(GRID)
        val YO = 136            // 기준점 Y좌표(GRID)
        val DEGRAD = Math.PI / 180.0
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = v2 * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * Math.sin(theta) + XO + 0.5).toInt()
        val y = (ro - ra * Math.cos(theta) + YO + 0.5).toInt()

        return LocationXY(x, y)
    }
}