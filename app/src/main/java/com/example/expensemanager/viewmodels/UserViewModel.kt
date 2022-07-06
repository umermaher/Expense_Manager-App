package com.example.expensemanager.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.repositories.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository): ViewModel(){
    var googleSignInTask:MutableLiveData<Task<AuthResult>> = MutableLiveData()
    val auth=userRepository.auth

    fun handleGoogleSignInTask(completedTask: Task<GoogleSignInAccount>) = viewModelScope.launch {
        userRepository.handleGoogleSignInTask(completedTask,googleSignInTask)
    }
}