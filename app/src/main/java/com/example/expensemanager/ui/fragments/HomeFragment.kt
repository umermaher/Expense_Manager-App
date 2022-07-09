package com.example.expensemanager.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.models.User
import com.example.expensemanager.ui.AddTransactionActivity
import com.example.expensemanager.utils.PrefsData
import com.example.expensemanager.utils.UPDATED_USER
import com.example.expensemanager.utils.USER
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private lateinit var user:User

    companion object{
        private const val ADD_REQUEST_CODE=98
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=FragmentHomeBinding.inflate(layoutInflater)


        viewModel= getUserViewModel(this,requireActivity().application)

        binding.helloTv.text="Hello, ${PrefsData.getUserName(requireContext())}"

        getCurrentUser()

        binding.addTransactionFab.setOnClickListener {
            val intent=Intent(requireActivity(), AddTransactionActivity::class.java)
            intent.putExtra(USER,user)
            startActivityForResult(intent, ADD_REQUEST_CODE)
        }

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getCurrentUser() {
        binding.homePb.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            user= viewModel.getCurrentUser()
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
        if(PrefsData.isFirstTime(requireContext())){
            PrefsData.saveUserName(requireContext(),user.displayName)
            PrefsData.setNotFirstTime(requireContext())
            binding.helloTv.text="Hello, ${user.displayName}"
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            if(requestCode== ADD_REQUEST_CODE){
                data?.let {
                    user=data.getSerializableExtra(UPDATED_USER) as User
                    updateUI()
                }
            }
        }
    }
}
