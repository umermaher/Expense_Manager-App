package com.example.expensemanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expensemanager.R
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.models.User
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private lateinit var user:User
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=FragmentHomeBinding.inflate(layoutInflater)
        viewModel= getUserViewModel(this)
        mAuth=Firebase.auth

        initializeUser()

        binding.addTransactionFab.setOnClickListener {
            startActivity(Intent(requireActivity(),AddTransactionActivity::class.java))
        }
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initializeUser() {
        binding.homePb.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            user=viewModel.getUserById(mAuth.uid!!).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.expenseTv.text= user.expense.toString()
        binding.incomeTv.text=user.income.toString()
        binding.balanceTv.text=user.balance.toString()
        binding.homePb.visibility=View.GONE
    }
}