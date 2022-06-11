package com.first.ridingpartnerinsiheung.views.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R

class RecordDialog(context: Context) {

    private val dialog = Dialog(context)

    fun start(distance : String, averSpeed : String, timer : String){
        setDialog()
        initView(distance, averSpeed, timer)
    }
    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_record)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        dialog.show()
    }
    @SuppressLint("SetTextI18n")
    private fun initView(distance : String, averSpeed : String, timer : String){
        val distanceTv = dialog.findViewById<TextView>(R.id.distance)
        val averSpeedTv = dialog.findViewById<TextView>(R.id.averSpeed)
        val timerTv = dialog.findViewById<TextView>(R.id.timer)

        distanceTv.text = "주행 거리 : ${distance} km"
        averSpeedTv.text = "평균 속도 : ${averSpeed} km/s"
        timerTv.text = "주행 시간 : ${timer}"
    }
}