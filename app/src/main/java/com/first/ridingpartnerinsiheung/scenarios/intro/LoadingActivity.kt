package com.first.ridingpartnerinsiheung.scenarios.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.first.ridingpartnerinsiheung.R
import com.first.ridingpartnerinsiheung.scenarios.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collect

class LoadingActivity : AppCompatActivity() {

    // Firebase
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val viewModel by viewModels<LoadingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // 로그인 시도
        viewModel.tryLogin(this)

        lifecycleScope.launchWhenCreated {
            viewModel.loginResult.collect{ isLogin ->
                if (isLogin){
                    // 로그인 완료
                    toMainActivity(auth.currentUser)
                }else{
                    // 로그인 안되어있을 때 회원가입 페이지로
                    startActivity(Intent(this@LoadingActivity, SignActivity::class.java))
                }
            }
        }
    }
    private fun toMainActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
}