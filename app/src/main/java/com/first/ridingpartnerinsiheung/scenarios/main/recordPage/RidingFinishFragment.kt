package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.RidingFinishFragmentBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RidingFinishFragment : Fragment() {

    private lateinit var binding: RidingFinishFragmentBinding
    private val viewModel by viewModels<RecordViewModel>()

    private var memo : String? = ""
    private var data : RidingData? = null

    private var time : String? = null
    private val REQUEST_CODE:Int = 99
    private var imageUri :Uri? = null

    // Firebase
    private val auth = Firebase.auth
    private val user = auth.currentUser!!.uid
    private var dbStorage = FirebaseStorage.getInstance()
    private val storageRef = dbStorage.reference
    private val dbStore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        time = arguments?.getString("time")
        data = arguments?.getSerializable("data") as RidingData

        initBinding()
        initTextChanged()
        initClickListener()
        finishClickListener()
        initData(time!!, data!!)

        return binding.root
    }
    private fun initBinding(
        inflater: LayoutInflater = this.layoutInflater,
        container: ViewGroup? = null
    ) {
        binding = DataBindingUtil.inflate(inflater, R.layout.riding_finish_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun finishClickListener() {
        binding.complete.setOnClickListener {
            val recordActivity = activity as RecordActivity
            viewModel.memo.value = memo.toString()
            recordActivity.setFragment(RecordFragment(), time!!, data!!)
            addResultImage()
            saveData()
        }
    }
    private fun initTextChanged(){
        binding.memoET.doAfterTextChanged {
            memo = it.toString()
            saveData()
        }
    }
    private fun saveData(){
        memo?.let {
            dbStore.collection(user)
                .document("Massage").collection(time!!)
                .document("a")
                .set(memo!!)
                .addOnSuccessListener {
                    showToast("")
                }
                .addOnFailureListener{
                    showToast("")
                }
        }
    }

    private fun initData(time : String, data: RidingData){
        viewModel.savedTimer.value = data.timer
        viewModel.savedSpeed.value = data.averSpeed
        viewModel.savedKcal.value = data.kcal
        viewModel.savedDistance.value = data.sumDistance
        viewModel.savedTime.value = time
    }

    // 권한 부여
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var isAllGranted = true
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) { //권한 거부
                            isAllGranted = false
                            break
                        }
                        if (isAllGranted) {
                        } //권한 동의
                        else { //권한 거부했을경우
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(),
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) ||
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                requestPermission()
                            } else {
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    private var getContent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                imageUri = it
                Glide.with(this)
                    .load(it)
                    .into(binding.cImage)
                binding.cImage.clipToOutline
            }
        }
    }

    private fun setImage(){
        if (requestPermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            getContent.launch(intent)
            addResultImage()
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
    private fun initClickListener() {
        // 갤러리에서 이미지 불러오기
        binding.cImage.setOnClickListener {
            setImage()
        }
    }

    private fun addResultImage(){
        val fileName = "$user$time.png"
        imageUri?.let {
            storageRef.child(user).child(fileName).putFile(it)
                .addOnSuccessListener {
                    showToast("사진 저장에 성공")
                }
                .addOnFailureListener{
                    showToast("사진 저장에 실패하였습니다.")
                }
        }
    }
}