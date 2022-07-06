package com.example.expensemanager.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.LoginActivity
import com.example.expensemanager.repositories.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository): ViewModel(){
    var signInUpTask:MutableLiveData<Task<AuthResult>> = MutableLiveData()

    fun handleGoogleSignInTask(completedTask: Task<GoogleSignInAccount>) = viewModelScope.launch {
        userRepository.handleGoogleSignInTask(completedTask,signInUpTask)
    }
    fun handleSignUpTask(email:String,password:String)=viewModelScope.launch (Dispatchers.IO){
        userRepository.handleSignUpTask(signInUpTask,email,password)
    }
    fun handleLoginTask(email: String,password: String) = viewModelScope.launch (Dispatchers.IO){
        userRepository.handleLoginTask(signInUpTask,email,password)
    }
}