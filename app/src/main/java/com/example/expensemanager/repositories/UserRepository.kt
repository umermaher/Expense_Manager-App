package com.example.expensemanager.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository {
    val mAuth= Firebase.auth

    suspend fun handleSignUpTask(signUpTask: MutableLiveData<Task<AuthResult>>,email:String,password:String){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            signUpTask.postValue(it)
        }
    }
    suspend fun handleLoginTask(signInUpTask: MutableLiveData<Task<AuthResult>>, email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            signInUpTask.postValue(it)
        }
    }
    suspend fun handleGoogleSignInTask(
        completedTask: Task<GoogleSignInAccount>,
        googleSignInTask: MutableLiveData<Task<AuthResult>>
    ) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(LoginActivity.TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!,googleSignInTask)
        } catch (e: ApiException) {
            Log.w(LoginActivity.TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, googleSignInTask: MutableLiveData<Task<AuthResult>>) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = mAuth.signInWithCredential(credential).addOnCompleteListener {
            googleSignInTask.postValue(it)
        }
    }
}