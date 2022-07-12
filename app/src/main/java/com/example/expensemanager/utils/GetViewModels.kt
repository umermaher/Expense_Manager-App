package com.example.expensemanager.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.expensemanager.repositories.UserRepository
import com.example.expensemanager.viewmodels.UserViewModel
import com.example.expensemanager.viewmodels.UserViewModelProviderFactory

fun getUserViewModel(owner: ViewModelStoreOwner,app: Application):UserViewModel{
    val newsRepository= UserRepository()
    val newsViewModelProviderFactory= UserViewModelProviderFactory(app,newsRepository)
    return ViewModelProvider(owner,newsViewModelProviderFactory)[UserViewModel::class.java]
}
fun trimName(name: String): String {
    var value = ""
    for(i in name.indices){
        if(name[i]==' ') break
        value+=name[i]
    }
    return value
}