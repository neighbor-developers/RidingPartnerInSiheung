package com.first.ridingpartnerinsiheung.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import com.first.ridingpartnerinsiheung.R

class BackDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListenertoBack

    private lateinit var okBtn : Button
    private lateinit var cancelBtn : Button

    fun start(){
        setDialog()
        initView()
        initClickListener()
    }
    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_back)
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
    interface DialogOKCLickListenertoBack {
        fun onOKClicked()
    }
    fun setOnClickListener(listener: DialogOKCLickListenertoBack){
        onClickListener = listener
    }
}