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
    fun saveUserName(context: Context,name:String) {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        val editor=sp.edit()
        val trim=trimName(name)
        editor.putString(USER_NAME,trim)
        editor.apply()
    }

    private fun trimName(name: String): String {
        var value = ""
        for(i in name.indices){
            if(name[i]==' ') break
            value+=name[i]
        }
        return value
    }

    fun getUserName(context: Context): String? {
        val sp=context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(USER_NAME,"...")
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