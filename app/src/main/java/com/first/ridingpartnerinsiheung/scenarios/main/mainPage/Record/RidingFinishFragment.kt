package com.first.ridingpartnerinsiheung.scenarios.main.mainPage.Record

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.first.ridingpartnerinsiheung.databinding.RidingFinishFragmentBinding
import com.first.ridingpartnerinsiheung.scenarios.main.mainPage.MainActivity

class RidingFinishFragment : Fragment() {
    private lateinit var binding: RidingFinishFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = RidingFinishFragmentBinding.inflate(inflater, container, false)
        finishClickListener()
        startingRecord()
        return binding.root
    }

    private fun finishClickListener() {
        binding.complete.setOnClickListener {
            activity?.let {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun startingRecord() {
        binding.memoBtn.setOnClickListener {
            (activity as MainActivity).setFrag(RecordingFragment())
        }
        binding.addMemo.setOnClickListener {
            (activity as MainActivity).setFrag(RecordingFragment())
        }
    }
}