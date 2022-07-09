package com.example.expensemanager.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanager.repositories.UserRepository

class UserViewModelProviderFactory(val app:Application,private val userRepository: UserRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = UserViewModel(app,userRepository) as T
}