package com.example.expensemanager.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.models.User
import com.example.expensemanager.repositories.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine

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

    fun getUserById(uid:String): Task<DocumentSnapshot> = userRepository.getUserById(uid)

    fun addUser(user: User) = viewModelScope.launch (Dispatchers.IO){
        userRepository.addUser(user)
    }
}