package com.first.ridingpartnerinsiheung.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R

class RidingSaveDialog (context: Context){
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    private lateinit var okBtn : Button
    private lateinit var cancelBtn : Button

    fun start(){
        setDialog()
        initView()
        initClickListener()
    }
    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_riding_save)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun initView(){
        okBtn = dialog.findViewById(R.id.okBtn)
        cancelBtn = dialog.findViewById(R.id.cancelBtn)

    }
    private fun initClickListener(){
        okBtn.setOnClickListener {
            onClickListener.onOKClicked()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface DialogOKCLickListener {
        fun onOKClicked()
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}