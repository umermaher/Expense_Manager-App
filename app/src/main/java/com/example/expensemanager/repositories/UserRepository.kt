package com.example.expensemanager.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.expensemanager.ui.activities.LoginActivity
import com.example.expensemanager.models.User
import com.example.expensemanager.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserRepository {
    val mAuth= Firebase.auth
    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

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

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun firebaseAuthWithGoogle(idToken: String, googleSignInTask: MutableLiveData<Task<AuthResult>>) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task->
            if(task.isSuccessful){
                val user=task.result.user
                if(user!=null) {
                    GlobalScope.launch {
                        try {
                            val model = getUserById(user.uid).await().toObject(User::class.java)!!
                        } catch (e: Exception) {
                            val model = User(
                                user.uid,
                                user.email!!,
                                user.displayName!!,
                                user.photoUrl.toString()
                            )
                            addUser(model)
                        }
                        withContext(Dispatchers.Main) {
                            googleSignInTask.postValue(task)
                        }
                    }
                }
            }
        }
    }

    fun getUserById(uid:String): Task<DocumentSnapshot> = usersCollection.document(uid).get()

    suspend fun addUser(user: User) = usersCollection.document(user.uid).set(user)

    fun getCurrentUser(): Task<DocumentSnapshot> = usersCollection.document(mAuth.uid!!).get()
    fun updateUser(user: User,task:MutableLiveData<Resource<String>>) {
        usersCollection.document(user.uid).set(user).addOnSuccessListener {
            task.postValue(Resource.Success("Done"))
        }.addOnFailureListener {
            task.postValue(Resource.Error("Failed to add transaction! : ${it.message}"))
        }
    }

    suspend fun sendPasswordResetEmail(email: String,fpTask:MutableLiveData<Resource<String>>) {
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            fpTask.postValue(Resource.Success("Link for password recovery has been sent to your email"))
        }.addOnFailureListener {
            fpTask.postValue(Resource.Error("No such account exist!"))
        }
    }

}