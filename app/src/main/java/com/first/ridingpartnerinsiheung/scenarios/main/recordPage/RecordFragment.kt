package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.FragmentRecordBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class RecordFragment : Fragment() {
    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    private var dbStorage = FirebaseStorage.getInstance()
    private val storageRef = dbStorage.reference
    private var data : RidingData? = null

    private lateinit var binding: FragmentRecordBinding
    private val viewModel by viewModels<RecordViewModel>()

    var a:Int=0;
    var b:Int=0;
    var time : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding()
        data = arguments?.getSerializable("data") as RidingData

        arguments?.let{
            time = it.getString("time")
            if(time != ""){
                viewModel.changeTime(time!!)
                getDiaryImage(time!!)

            }
        }

        showToast("사진을 캡쳐해 사용하세요!")

        clickHomeButtonListener()
        changeColorButtonListener()
        initData(data!!)

        return binding.root
    }
    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initData(data: RidingData){
        viewModel.savedDistance.value = data.sumDistance
        binding.distance.text= "${viewModel.distanceText}"
    }

    private fun changeColorButtonListener(){
        binding.changePictureColor.setOnClickListener {
            a=1-a;
            if(a==0){
                val matrix: ColorMatrix = ColorMatrix()
                matrix.setSaturation(0F)
                val filter: ColorMatrixColorFilter = ColorMatrixColorFilter(matrix)
                binding.picture.setColorFilter(filter)
                binding.changePictureColor.setImageResource(R.drawable.color_64)
                // binding.changePictureColor.setPadding(0)
            }
            else{
                binding.picture.setColorFilter(null)
                binding.changePictureColor.setImageResource(R.drawable.color_64_black2)
                // binding.changePictureColor.setPadding(20)
            }
        }

        binding.changeTextColor.setOnClickListener {
            b=1-b;
            if(b==0){
                binding.date.setTextColor(Color.WHITE)
                binding.memo.setTextColor(Color.WHITE)
                binding.distance.setTextColor(Color.WHITE)
                binding.changeTextColor.setImageResource(R.drawable.color_dropper_white)
            }
            else {
                binding.date.setTextColor(Color.BLACK)
                binding.memo.setTextColor(Color.BLACK)
                binding.distance.setTextColor(Color.BLACK)
                binding.changeTextColor.setImageResource(R.drawable.color_dropper_black)
            }
        }
    }

    private fun clickHomeButtonListener(){
        binding.backButton.setOnClickListener{
            val intent= Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // firebase 이미지  가져오기
    private fun getDiaryImage(time : String) {
        val fileName = "$user$time.png" // time은 페이지 바꾸면서 데이터 넣기
        storageRef.child(user).child(fileName).downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .into(binding.picture)
                binding.picture.clipToOutline
            }
            .addOnFailureListener {
                binding.picture.setImageResource(R.drawable.loding_page_color)
            }
    }
}


