package com.first.ridingpartnerinsiheung.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Fragment.showToast(message: String){
    requireActivity().showToast(message)
}
