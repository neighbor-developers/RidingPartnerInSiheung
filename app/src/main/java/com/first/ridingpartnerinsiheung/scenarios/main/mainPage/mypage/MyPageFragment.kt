package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.mypage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.MySharedPreferences
import com.first.ridingpartnerinsiheung.databinding.FragmentMyPageBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.first.ridingpartnerinsiheung.scenarios.main.recordPage.RecordFragment
import com.first.ridingpartnerinsiheung.views.dialog.ChangeNameDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect


class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private val viewModel by viewModels<MyPageViewModel>()
    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser

    companion object {
        private lateinit var prefs: MySharedPreferences
    }

    private val userImageView : ImageView by lazy { binding.userImage }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        prefs = MySharedPreferences((activity as MainActivity).applicationContext)

        initBinding()
        initObserves()
        showUserProfile()
        initClickListener()

        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater = this.layoutInflater, container: ViewGroup? = null){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initObserves(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.event.collect(){ event ->
                when(event){
                    MyPageViewModel.MyPageEvent.ShowImage -> setImage()
                    MyPageViewModel.MyPageEvent.ShowChangeNameDialog -> showDialog()
                }
            }
        }
    }

    private fun showUserProfile(){
        // 저장된 이름이 존재할시 해당 이름 노출, 존재하지 않는다면 구글계정 이름 노출
        viewModel.userName.value = if (prefs.accountName != "") {
            prefs.accountName!!
        } else {
            user!!.displayName!!
        }
        // 저장된 이미지가 존재할시 해당 이미지 노출
        // 존재하지 않는다면  현재 로그인한 사용자 정보를 통해 PhotoUrl 가져오기

        val userImage = if (prefs.accountImage !="") {
            prefs.accountImage
        } else {
           user!!.photoUrl }

        Glide.with(this)
            .load(userImage)
            .error(R.drawable.basic_account)
            .into(userImageView)
    }

    private fun showDialog(){
        val dialog = ChangeNameDialog(requireContext())
        dialog.start()
        dialog.setOnClickListener(object:ChangeNameDialog.DialogOKCLickListener{
            override fun onOKClicked(name: String?) {
                viewModel.userName.value = name
                prefs.accountName = name
            }
        })
    }

    private fun initClickListener(){
        // 기록페이지로 이동
        binding.showRecordBtn.setOnClickListener {
            (activity as MainActivity).setFrag(RecordFragment())
        }
    }

    private fun setImage(){
        if (requestPermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
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
    // 권한요청
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

    private val REQUEST_CODE =99

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
}