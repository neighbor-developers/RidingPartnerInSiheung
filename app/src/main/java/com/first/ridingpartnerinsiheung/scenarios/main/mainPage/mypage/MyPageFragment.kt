package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.databinding.FragmentMyPageBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.recordPage.RecordFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        showUserProfile()
        initClickListener()

        return binding.root
    }

    private fun showUserProfile(){
        val userName = binding.userName
        val userEmail = binding.userEmail
        val userImage = binding.userImage


        // 이름, 이메일 설정
        userName.text = user!!.displayName;
        userEmail.text = user.email;

        //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
        val url = user.photoUrl.toString()
        Glide.with(this).load(url).error(R.drawable.basic_account).into(userImage);
    }

    private fun initClickListener(){
        binding.editNameBtn.setOnClickListener {
            // (activity as MainActivity).setFrag(PathListFragment())
        }
        // 기록페이지로 변경
        binding.showRecordBtn.setOnClickListener {
            (activity as MainActivity).setFrag(RecordFragment())
        }
        // 돌아가기
        binding.goBackButton.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }
    }
}