package com.first.ridingpartnerinsiheung.scenarios.main.recordPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.databinding.RidingFinishFragmentBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity

class RidingFinishFragment : Fragment() {
    private lateinit var binding: RidingFinishFragmentBinding
    private var memo : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RidingFinishFragmentBinding.inflate(inflater, container, false)

        initTextChanged()
        finishClickListener()

        return binding.root
    }

    private fun finishClickListener() {
        binding.complete.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }
    private fun initTextChanged(){
        binding.memoET.doAfterTextChanged {
            memo = it.toString()
        }
    }
}