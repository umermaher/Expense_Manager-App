package com.example.expensemanager.utils

import com.google.android.material.textfield.TextInputLayout


fun validateFullName(fullNameText:TextInputLayout): Boolean {
    val value = fullNameText.editText!!.text.toString()
    return if (value.isEmpty()) {
        fullNameText.error = "Fields cannot be empty"
        false
    }else if(value.matches(".*\\d.*".toRegex())){
        fullNameText.error="Remove numbers!"
        false
    } else {
        fullNameText.error = null
        fullNameText.isErrorEnabled = false
        true
    }
}
fun validateEmail(emailText:TextInputLayout): Boolean{
    val value=emailText.editText!!.text.toString()
    val test:Boolean= android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()

    return if(value.isEmpty()){
        emailText.error = "Fields cannot be empty"
        false
    }else if (!test){
        emailText.error="Invalid email address"
        false
    }else{
        emailText.error = null;
        emailText.isErrorEnabled = false;
        true
    }
}

fun validatePassword(passwordText: TextInputLayout):Boolean{
    val value=passwordText.editText!!.text.toString()
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$"

    return if(value.isEmpty()){
        passwordText.error = "Fields cannot be empty"
        false
    }else if(!value.matches(pattern.toRegex())){
        passwordText.error = "Password is too weak"
        false
    }else{
        passwordText.error = null
        passwordText.isErrorEnabled = false
        true
    }
}