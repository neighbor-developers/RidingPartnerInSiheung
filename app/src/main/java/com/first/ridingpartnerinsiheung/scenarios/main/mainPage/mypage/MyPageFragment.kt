package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.MySharedPreferences
import com.first.ridingpartnerinsiheung.databinding.FragmentMyPageBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.recordPage.RecordFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File


class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val REQUEST_CODE =99
    companion object {
        lateinit var prefs: MySharedPreferences
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = MySharedPreferences((activity as MainActivity).applicationContext)
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        showUserProfile()
        initClickListener()

        return binding.root
    }

    private fun showUserProfile(){
        val userName = binding.userName
        val userEmail = binding.userEmail
        val userImage = binding.userImage

        // 이메일 설정
        userEmail.text = user!!.email;

        // 저장된 이름이 존재할시 해당 이름 노출, 존재하지 않는다면 구글계정 이름 노출
        if (prefs.accountName != "") {
            userName.text = prefs.accountName
        } else {
            userName.text = user.displayName;
        }

        // 저장된 이미지가 존재할시 해당 이미지 노출
        // 존재하지 않는다면 구글계정 이미지 노출
        if (prefs.accountImage != "") {
            Glide.with(this)
                .load(prefs.accountImage)
                .error(R.drawable.basic_account)
                .into(userImage)
        } else {
            //현재 로그인한 사용자 정보를 통해 PhotoUrl 가져오기
            val url = user.photoUrl.toString()
            Glide.with(this)
                .load(url)
                .error(R.drawable.basic_account)
                .into(userImage);
        }
    }

    private fun initClickListener(){
        binding.userImage.setOnClickListener {
            setImage()
        }
        binding.editNameBtn.setOnClickListener {
            if (binding.changeName.visibility == View.GONE) {
                binding.changeName.visibility = View.VISIBLE;
            } else {
                binding.changeName.visibility = View.GONE;
            }
        }
        binding.changeNameBtn.setOnClickListener {
            binding.userName.text = binding.newName.text;
            prefs.accountName = binding.newName.text.toString();
            binding.changeName.visibility = View.GONE;
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
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    // 권한요청
    private fun requestPermission(): Boolean {
        val writePermission: Int = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission: Int = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        } else (return false)
    }

    private fun setImage(){
        if (requestPermission()) {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            getContent.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                (requireActivity()), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    private var getContent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.userImage)
                binding.userImage.clipToOutline

                prefs.accountImage = it.toString()
            }
        }
    }
}