package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.Record

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.databinding.FragmentRecordingBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RecordingFragment : Fragment() {
    private lateinit var binding: FragmentRecordingBinding

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingBinding.inflate(inflater, container, false)

//        finishWriting()
        return binding.root
    }

//    private fun finishWriting() {
//        binding.complete.setOnClickListener {
//            addDiaryContent(onSuccess = {showToast("저장 완료")}, onFailure = {showToast("저장 실패")})
//            activity?.let {
//                val intent = Intent(context, MainActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }
//
//    fun addDiaryContent(onSuccess: () -> Unit, onFailure: () -> Unit) {
//        val data = hashMapOf("contents" to diaryContent.value)
//        db.collection("UsersData")
//            .document(user).collection("Diary")
//            .document(date.value.toString())
//            .set(data)
//            .addOnSuccessListener {
//                postSuccess()
//            }
//            .addOnFailureListener {
//                postFailuer(it)
//            }
//    }
}