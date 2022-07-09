package com.example.expensemanager.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object PrefsData {
    fun clear(context: Context){
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
        editor.clear()
        editor.apply()
    }
    fun saveUserName(context: Context,name:String) {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
        editor.putString(USER_NAME,name)
        editor.apply()
    }
    fun getUserName(context: Context): String? {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(USER_NAME,"Sir")
    }
    fun isFirstTime(context: Context): Boolean {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getBoolean("isFirstTime",true)
    }
    fun setNotFirstTime(context: Context){
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
        editor.putBoolean("isFirstTime",false)
        editor.apply()
    }
}