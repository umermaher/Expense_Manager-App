package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanager.databinding.ActivitySignUpBinding
import com.example.expensemanager.repositories.UserRepository
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.utils.validateEmail
import com.example.expensemanager.utils.validateFullName
import com.example.expensemanager.utils.validatePassword
import com.example.expensemanager.viewmodels.UserViewModel
import com.example.expensemanager.viewmodels.UserViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.view.*

class SignUpActivity : AppCompatActivity() {
    private var _binding:ActivitySignUpBinding?=null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = getUserViewModel(this)

        binding.signUpBtn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        binding.signUpPb.visibility=View.VISIBLE
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
            bothAreSame ->{
                binding.signUpConfirmPasswordText.error = "Confirm your password correctly!"
                return
            }
            else -> {}
        }
//        Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()
        val name = binding.signUpNameText.editText!!.text.toString()
        val email=binding.signUpEmailText.editText!!.text.toString()
        val password=binding.signUpPasswordText.editText!!.text.toString()

        viewModel.handleSignUpTask(email,password)

        viewModel.signInUpTask.observe(this){
            if(it.isSuccessful){
                updateUI(it.result.user)
            }else{
                Toast.makeText(this,"Login Failed: ${it.exception}!", Toast.LENGTH_LONG).show()
                binding.signUpPb.visibility=View.GONE
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            binding.signUpPb.visibility=View.GONE
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    fun login(view: View) {onBackPressed()}
    fun back(view: View) {onBackPressed()}
}