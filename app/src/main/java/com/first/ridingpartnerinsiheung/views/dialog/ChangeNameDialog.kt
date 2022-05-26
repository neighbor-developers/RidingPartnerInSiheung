package com.first.ridingpartnerinsiheung.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.first.ridingpartnerinsiheung.R

class ChangeNameDialog (context : Context){

    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    lateinit var data : String

    private val modifyEditText: EditText by lazy {  dialog.findViewById(R.id.modifyET)}
    private val okBtn : Button by lazy{ dialog.findViewById(R.id.okModify)}
    private val cancelBtn : Button by lazy { dialog.findViewById(R.id.cancelModify)}

    fun start() {
        setDialog()
        initClickListener()
        initTextChangeListener()
    }
    private fun setDialog() {
        dialog.setContentView(R.layout.dialog_change_name)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun initTextChangeListener() {
        modifyEditText.doAfterTextChanged {
            data = it.toString()
        }
    }
    private fun initClickListener(){
        okBtn.setOnClickListener {
            onClickListener.onOKClicked(data)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface DialogOKCLickListener {
        fun onOKClicked(name : String?)
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}