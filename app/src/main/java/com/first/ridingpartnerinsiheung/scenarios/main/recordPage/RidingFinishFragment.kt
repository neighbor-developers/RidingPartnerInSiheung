package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.data.RidingData
import com.first.ridingpartnerinsiheung.databinding.RidingFinishFragmentBinding
import com.first.ridingpartnerinsiheung.extensions.showToast
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RidingFinishFragment : Fragment() {

    private lateinit var binding: RidingFinishFragmentBinding
    private val viewModel by viewModels<RecordViewModel>()

    private var memo : String? = ""
    private var data : RidingData? = null

    private var time : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        time = arguments?.getString("time")
        data = arguments?.getSerializable("data") as RidingData

        initBinding()
        initTextChanged()
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
        }
    }
    private fun initTextChanged(){
        binding.memoET.doAfterTextChanged {
            memo = it.toString()
        }
    }
    private fun initData(time : String, data: RidingData){
        viewModel.savedTimer.value = data.timer
        viewModel.savedSpeed.value = data.averSpeed
        viewModel.savedKcal.value = data.kcal
        viewModel.savedDistance.value = data.sumDistance
        viewModel.savedTime.value = time
    }
}