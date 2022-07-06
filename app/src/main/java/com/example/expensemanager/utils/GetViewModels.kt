package com.example.expensemanager.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.expensemanager.repositories.UserRepository
import com.example.expensemanager.viewmodels.UserViewModel
import com.example.expensemanager.viewmodels.UserViewModelProviderFactory

fun getUserViewModel(owner: ViewModelStoreOwner):UserViewModel{
    val newsRepository= UserRepository()
    val newsViewModelProviderFactory= UserViewModelProviderFactory(newsRepository)
    return ViewModelProvider(owner,newsViewModelProviderFactory)[UserViewModel::class.java]
}