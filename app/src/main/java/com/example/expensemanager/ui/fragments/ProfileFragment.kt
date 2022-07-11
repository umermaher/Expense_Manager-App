package com.example.expensemanager.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ChangePasswordBsBinding
import com.example.expensemanager.databinding.FragmentProfileBinding
import com.example.expensemanager.models.User
import com.example.expensemanager.ui.activities.LoginActivity
import com.example.expensemanager.utils.InternetConnectivityLiveData
import com.example.expensemanager.utils.PrefsData
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.utils.validatePassword
import com.example.expensemanager.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*

class ProfileFragment : Fragment() {
    private lateinit var _binding:FragmentProfileBinding
    private val binding get() = _binding
    private lateinit var viewModel:UserViewModel
    private lateinit var user: User
    private var signOutMsgDialog:AlertDialog?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentProfileBinding.inflate(layoutInflater)

        viewModel= getUserViewModel(this,requireActivity().application)

        InternetConnectivityLiveData(requireContext()).observe(viewLifecycleOwner){
            if(it) getCurrentUser()
        }

        if(PrefsData.isSignedInWithGoogle(requireContext()))
            binding.changePasswordBtn.isEnabled=false

        binding.logoutBtn.setOnClickListener {
            signOut()
        }

        binding.changePasswordBtn.setOnClickListener {
            changePassword()
        }

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getCurrentUser() {
        binding.profilePb.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            user= viewModel.getCurrentUser()
            withContext(Dispatchers.Main){
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.profilePb.visibility=View.GONE
        binding.nameTv.text=user.displayName
        binding.emailTv.text=user.email
        if(user.imageUrl.isNotEmpty())
            Glide.with(this).load(user.imageUrl).circleCrop().into(binding.userProfile)
    }

    private fun changePassword(){
        val bsdBinding=ChangePasswordBsBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bsdBinding.root)
        bottomSheetDialog.setCanceledOnTouchOutside(true)

        bsdBinding.changePassBtn.setOnClickListener {
            val bothAreSame = bsdBinding.changeText.editText!!.text.toString() ==
                    bsdBinding.changeConfirmText.editText!!.text.toString()
            val test1 = validatePassword(bsdBinding.changeText)
            val test2 = validatePassword(bsdBinding.changeConfirmText)
            when(false){
                test1 -> return@setOnClickListener
                test2 -> return@setOnClickListener
                bothAreSame ->{
                    bsdBinding.changeConfirmText.error = "Confirm your password correctly!"
                    return@setOnClickListener
                }
                else -> {}
            }
            val password = bsdBinding.changeText.editText!!.text.toString()
            binding.profilePb.visibility=View.VISIBLE
            GlobalScope.launch (Dispatchers.IO){
                Firebase.auth.currentUser!!.updatePassword(password)
                withContext(Dispatchers.Main){
                    binding.profilePb.visibility=View.VISIBLE

                    signOutMsgDialog=AlertDialog.Builder(requireContext())
                        .setMessage(getString(R.string.change_pass_msg))
                        .setCancelable(false)
                        .setPositiveButton("ok") {_,_ ->
                            signOut()
                        }.show()
                }
            }
        }

        bottomSheetDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun signOut() {
        profilePb.visibility= View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            Firebase.auth.signOut()
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),"SIGN OUT!!", Toast.LENGTH_LONG).show()
                PrefsData.clear(requireContext())
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onStop() {
        signOutMsgDialog?.let {
            if(signOutMsgDialog!!.isShowing)
                signOut()
        }
        super.onStop()
    }
}