package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Pair
import android.app.ActivityOptions
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanager.databinding.ActivityLoginBinding
import com.example.expensemanager.repositories.UserRepository
import com.example.expensemanager.utils.validateEmail
import com.example.expensemanager.utils.validatePassword
import com.example.expensemanager.viewmodels.UserViewModel
import com.example.expensemanager.viewmodels.UserViewModelProviderFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private var _binding:ActivityLoginBinding?=null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: UserViewModel
    companion object {
        private const val RC_SIGN_IN=99
        const val TAG="SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureGoogleSignIn()

        val userRepository= UserRepository()
        val newsViewModelProviderFactory= UserViewModelProviderFactory(userRepository)
        viewModel = ViewModelProvider(this,newsViewModelProviderFactory)[UserViewModel::class.java]

        binding.loginBtn.setOnClickListener {
            customSignIn()
        }
        binding.loginPasswordText.setOnClickListener{
            binding.loginPasswordText.passwordVisibilityToggleRequested(true)
        }
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

    private fun customSignIn() {
        val test1= validateEmail(binding.loginEmailText)
        val test2= validatePassword(binding.loginPasswordText)
        when(false){
            test1 -> return
            test2 -> return
            else -> {}
        }

        
    }

    fun signInWithGoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> {
                binding.loginPb.visibility=View.VISIBLE
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                firebaseAuthWithGoogle(task)
            }
        }
    }

    private fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModel.handleGoogleSignInTask(task)
        viewModel.googleSignInTask.observe(this){
            if(it.isSuccessful){
                updateUI(it.result.user)
            }else{
                Snackbar.make(binding.root,"Login Failed!",Snackbar.LENGTH_SHORT).show()
                binding.loginPb.visibility=View.GONE
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){
            binding.loginPb.visibility=View.GONE
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
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
