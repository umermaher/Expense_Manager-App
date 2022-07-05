package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Pair
import android.app.ActivityOptions
import android.util.Log
import com.example.expensemanager.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers

class LoginActivity : AppCompatActivity() {
    private var _binding:ActivityLoginBinding?=null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private companion object {
        private const val RC_SIGN_IN=99
        private const val TAG="SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGoogleSignIn()

    }

    private fun configureGoogleSignIn() {
        //configure google sign in
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)
        auth= Firebase.auth
    }

    fun signInWithGoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInTask(task)
            }
        }
    }

    private fun handleSignInTask(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

    }


    fun forgetPassword(view: View) {}
    fun register(view: View) {
        val intent=Intent(this, SignUpActivity::class.java)

//            startActivity(intent)
        val pair:Array<Pair<View,String>>? = arrayOf(
            Pair<View,String>(binding.loginIcon,getString(R.string.login_icon_tran)),
            Pair<View,String>(binding.loginTitle,getString(R.string.login_title_tran)),
            Pair<View,String>(binding.loginEmailText,getString(R.string.login_email_tran)),
            Pair(binding.loginPasswordText,getString(R.string.login_password_tran)),
            Pair<View,String>(binding.loginBtn,getString(R.string.login_btn_tran)),
            Pair<View,String>(binding.newUserText,getString(R.string.login_new_user_tran)),
            Pair<View,String>(binding.registerText,getString(R.string.login_register_tran))
        )

        val options: ActivityOptions =ActivityOptions.makeSceneTransitionAnimation(this,
            pair!![0], pair[1], pair[2], pair[3], pair[4], pair[5], pair[6]
        )
        startActivity(intent,options.toBundle())
    }
}
