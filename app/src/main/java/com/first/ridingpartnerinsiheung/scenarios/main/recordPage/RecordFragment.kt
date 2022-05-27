package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.first.ridingpartnerinsiheung.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class RecordFragment : Fragment() {
    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the dialog for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    private fun showRecord() {
        val data = db.collection("UsersData")
            .document(user).collection("Record")
            .get()
    }
}