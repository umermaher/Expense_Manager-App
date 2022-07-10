package com.example.expensemanager.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ActivityMainBinding
import com.example.expensemanager.utils.InternetConnectivityLiveData
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkingAndMonitoringInternet()

        binding.bottomNavView.setupWithNavController(fragmentContainerView.findNavController())
    }

    private fun checkingAndMonitoringInternet() {
        val connectivityLiveData= InternetConnectivityLiveData(this)
        //below condition is written to check the internet for the first time as the livedata hasn't contain postValue.
        if(!getUserViewModel(this,application).hasInternetConnection()) applyMsgAnim(AnimationUtils.loadAnimation(this, R.anim.net_msg_from_top),View.VISIBLE)
        // below livedata observance is for monitoring network connection
        connectivityLiveData.observe(this) { hasInternet ->
            if (hasInternet == false)
                applyMsgAnim(AnimationUtils.loadAnimation(this, R.anim.net_msg_from_top),View.VISIBLE)
            else {
                applyMsgAnim(AnimationUtils.loadAnimation(this, R.anim.net_msg_to_top), View.GONE)
            }
        }
    }

    private fun applyMsgAnim(loadAnimation: Animation, visibility: Int) {
        noConnectionMessage.startAnimation(loadAnimation)
        noConnectionMessage.visibility= visibility
    }
}