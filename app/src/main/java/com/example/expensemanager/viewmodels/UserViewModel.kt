package com.example.expensemanager.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.EmApplication
import com.example.expensemanager.models.User
import com.example.expensemanager.repositories.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine

class UserViewModel(app: Application, private val userRepository: UserRepository): AndroidViewModel(app){
    var signInUpTask:MutableLiveData<Task<AuthResult>> = MutableLiveData()
    var updateTask:MutableLiveData<Boolean> = MutableLiveData()
    var currentUser:MutableLiveData<User> = MutableLiveData()

    fun handleGoogleSignInTask(completedTask: Task<GoogleSignInAccount>) = viewModelScope.launch {
        userRepository.handleGoogleSignInTask(completedTask,signInUpTask)
    }
    fun handleSignUpTask(email:String,password:String)=viewModelScope.launch (Dispatchers.IO){
        userRepository.handleSignUpTask(signInUpTask,email,password)
    }
    fun handleLoginTask(email: String,password: String) = viewModelScope.launch (Dispatchers.IO){
        userRepository.handleLoginTask(signInUpTask,email,password)
    }

    fun addUser(user: User) = viewModelScope.launch (Dispatchers.IO){
        userRepository.addUser(user)
    }

    fun getUserById(uid:String): Task<DocumentSnapshot> = userRepository.getUserById(uid)

    suspend fun getCurrentUser(): User = userRepository.getCurrentUser().await().toObject(User::class.java)!!

    fun updateUser(user: User) = viewModelScope.launch {
        if(hasInternetConnection())
            userRepository.updateUser(user,updateTask)
        else{
            updateTask.postValue(false)
        }
    }

    fun hasInternetConnection():Boolean{
        val connectivityManager=getApplication<EmApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork= connectivityManager.activeNetwork ?: return false
            val networkCapabilities=connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                networkCapabilities.hasTransport(TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> true
                }
            }
        }
        return false
    }
}