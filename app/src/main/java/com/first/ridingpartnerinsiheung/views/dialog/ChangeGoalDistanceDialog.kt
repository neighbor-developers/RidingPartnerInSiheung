package com.first.ridingpartnerinsiheung.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import com.first.ridingpartnerinsiheung.R

class ChangeGoalDistanceDialog(context : Context){

    private val dialog = Dialog(context)
    private lateinit var onClickListener: DialogOKCLickListener

    var data : Int = 10

    private val okBtn : Button by lazy{ dialog.findViewById(R.id.okBtn)}
    private val cancelBtn : Button by lazy { dialog.findViewById(R.id.cancelBtn)}
    private val spinnerGoalDistance : Spinner by lazy { dialog.findViewById(R.id.spinnerGoal) }

    private val goalArray = arrayOf(5, 10, 20, 30, 40)

    fun start() {
        setDialog()
        initClickListener()
        initSpinnerListener()
    }
    private fun setDialog() {
        dialog.setContentView(R.layout.dialog_change_goal_distance)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun initSpinnerListener() {
        spinnerGoalDistance.adapter = ArrayAdapter(
            dialog.context,
            android.R.layout.simple_spinner_item,
            goalArray
        )

        spinnerGoalDistance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /* no-op */
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                data = spinnerGoalDistance.selectedItem as Int
            }
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
        fun onOKClicked(goal : Int)
    }
    fun setOnClickListener(listener: DialogOKCLickListener){
        onClickListener = listener
    }
}