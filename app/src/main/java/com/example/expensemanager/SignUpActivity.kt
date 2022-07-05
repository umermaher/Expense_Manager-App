package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.expensemanager.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private var _binding:ActivitySignUpBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpPasswordText.setOnClickListener{
            binding.signUpPasswordText.passwordVisibilityToggleDrawable?.setVisible(true,false)
        }


    }

    fun login(view: View) { onBackPressed()}
    fun back(view: View) {onBackPressed()}
}