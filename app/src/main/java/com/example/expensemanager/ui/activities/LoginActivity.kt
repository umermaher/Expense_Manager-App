package com.example.expensemanager.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ActivityLoginBinding
import com.example.expensemanager.utils.*
import com.example.expensemanager.viewmodels.SplashViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private var _binding:ActivityLoginBinding?=null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: UserViewModel
    private val splashViewModel:SplashViewModel by viewModels()
    companion object {
        private const val RC_SIGN_IN=99
        const val TAG="SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                splashViewModel.isLoading.value
            }
        }

        _binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        configureGoogleSignIn()

        viewModel = getUserViewModel(this,application)

        binding.loginBtn.setOnClickListener {
            customSignIn()
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun customSignIn() {
        binding.loginPb.visibility=View.VISIBLE
        val test1= validateEmail(binding.loginEmailText)
        val test2= validatePassword(binding.loginPasswordText)
        when(false){
            test1 -> return
            test2 -> return
            else -> {}
        }
        val email=binding.loginEmailText.editText!!.text.toString()
        val password=binding.loginPasswordText.editText!!.text.toString()

        viewModel.handleLoginTask(email,password)
        viewModel.signInUpTask.observe(this) {
            if(it.isSuccessful)
                updateUI(it.result.user)
            else{
                Toast.makeText(this,"Login Failed: ${it.exception}!",Toast.LENGTH_LONG).show()
                hidePb()
            }
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
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                firebaseAuthWithGoogle(task)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        binding.loginPb.visibility=View.VISIBLE
        viewModel.handleGoogleSignInTask(task)
        viewModel.signInUpTask.observe(this){ it ->
            if(it.isSuccessful){

                val firebaseUser=it.result.user
                firebaseUser?.let { user ->

                    PrefsData.saveUserData(this@LoginActivity,user.displayName!!,user.email!!)
                    PrefsData.yesSignedInWithGoogle(this)
                    updateUI(firebaseUser)

//                    GlobalScope.launch {
//
//                        val user = try{
//                            viewModel.getUserById(it.uid).await().toObject(User::class.java)!!
//                        }catch (e:Exception){
//                            val user=User(it.uid,it.email!!,it.displayName!!,it.photoUrl.toString())
//                            viewModel.addUser(user)
//                            user
//                        }
//                        withContext(Dispatchers.Main){
////                            Toast.makeText(this@LoginActivity,user.displayName,Toast.LENGTH_LONG).show()
//                            PrefsData.saveUserName(this@LoginActivity,user.displayName)
//                            updateUI(firebaseUser)
//                        }
//                    }
                }

            }else{
                Toast.makeText(this,"Login Failed: ${it.exception}!",Toast.LENGTH_LONG).show()
                hidePb()
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){
            hidePb()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun hidePb() { binding.loginPb.visibility=View.GONE }

    @OptIn(DelicateCoroutinesApi::class)
    fun forgetPassword(view: View) {
        if (!validateEmail(binding.loginEmailText))
            return
        val email = binding.loginEmailText.editText!!.text.toString()

        viewModel.sendPasswordResetEmail(email)
        viewModel.fpTask.observe(this) {
            when (it) {
                is Resource.Loading -> binding.loginPb.visibility = View.VISIBLE
                is Resource.Success -> {
                    hidePb()
                    Toast.makeText(this,it.data,Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    hidePb()
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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