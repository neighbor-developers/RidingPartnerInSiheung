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
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentRecodePictureBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class RecordFragment : Fragment() {
    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    private var storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference


    lateinit var binding: FragmentRecodePictureBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecodePictureBinding.inflate(inflater, container, false)

        binding.date.setTextColor(Color.BLACK)
        binding.memo.setTextColor(Color.BLACK)
        binding.distance.setTextColor(Color.BLACK)
        var a:Int=0;
        var b:Int=0;

        Toast.makeText(requireContext(), "사진을 캡쳐해 사용하세요!", Toast.LENGTH_SHORT).show();


        binding.backButton.setOnClickListener{
            val intent= Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

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
        return binding.root
    }
}


