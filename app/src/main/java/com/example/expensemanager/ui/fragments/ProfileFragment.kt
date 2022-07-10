package com.example.expensemanager.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.expensemanager.databinding.FragmentProfileBinding
import com.example.expensemanager.ui.activities.LoginActivity
import com.example.expensemanager.utils.PrefsData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*

class ProfileFragment : Fragment() {
    private lateinit var _binding:FragmentProfileBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentProfileBinding.inflate(layoutInflater)

        binding.logoutBtn.setOnClickListener {
            signOut()
        }

        return binding.root
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

}