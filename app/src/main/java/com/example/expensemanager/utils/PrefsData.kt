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
    fun yesSignedInWithGoogle(context: Context){
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
        editor.putBoolean("isSignedInWithGoogle",true)
        editor.apply()
    }
    fun isSignedInWithGoogle(context: Context):Boolean{
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getBoolean("isSignedInWithGoogle",false)
    }
    fun saveUserData(context: Context,name:String,email:String) {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
//        val trim=trimName(name)
        editor.putString(USER_NAME,name)
        editor.putString("email",email)
        editor.apply()
    }

    fun getUserName(context: Context): String? {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(USER_NAME,"...")
    }
    fun getEmail(context: Context):String?{
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getString("email","...")
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