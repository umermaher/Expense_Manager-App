package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.expensemanager.databinding.ActivitySignUpBinding
import com.example.expensemanager.utils.validateEmail
import com.example.expensemanager.utils.validateFullName
import com.example.expensemanager.utils.validatePassword

class SignUpActivity : AppCompatActivity() {
    private var _binding:ActivitySignUpBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpBtn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val bothAreSame = binding.signUpPasswordText.editText!!.text.toString() ==
                binding.signUpConfirmPasswordText.editText!!.text.toString()
        val test1=validateFullName(binding.signUpNameText)
        val test2=validateEmail(binding.signUpEmailText)
        val test3=validatePassword(binding.signUpPasswordText)
        val test4=validatePassword(binding.signUpConfirmPasswordText)

        when(false){
            test1 -> return
            test2 -> return
            test3 -> return
            test4 -> return
            bothAreSame -> return
            else -> {
            }
        }
        Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()
//        if(!validateFullName(binding.signUpNameText) || !validateEmail(binding.signUpEmailText) || !validatePassword(binding.signUpPasswordText)
//            || !validatePassword(binding.signUpConfirmPasswordText) || !bothAreSame)
//            return
    }

    fun login(view: View) { onBackPressed()}
    fun back(view: View) {onBackPressed()}
}