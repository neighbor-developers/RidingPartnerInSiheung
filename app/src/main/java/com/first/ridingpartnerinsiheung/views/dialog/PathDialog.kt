package com.first.ridingpartnerinsiheung.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.first.ridingpartnerinsiheung.R

class PathDialog(context: Context) {

    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    private lateinit var okBtn : Button
    private lateinit var cancelBtn : Button
    private lateinit var pathImageView: ImageView
    private lateinit var pathImageView2: ImageView

    private lateinit var pathNameTextView : TextView

    fun start(pathName : String, pathImage: Int, routeImage : Int){
        setDialog()
        initView(pathName, pathImage, routeImage)
        initClickListener()
    }
    private fun setDialog(){
        dialog.setContentView(R.layout.dialog_path)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun initView(pathName : String, pathImage: Int, routeImage: Int){
        okBtn = dialog.findViewById(R.id.okBtn)
        cancelBtn = dialog.findViewById(R.id.cancelBtn)
        pathImageView = dialog.findViewById(R.id.pathImage)
        pathNameTextView = dialog.findViewById(R.id.pathName)
        pathImageView2 = dialog.findViewById(R.id.routeImage)

        pathNameTextView.text = pathName
        pathImageView.setImageResource(pathImage)
        pathImageView2.setImageResource(routeImage)
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