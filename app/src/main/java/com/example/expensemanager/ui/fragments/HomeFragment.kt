package com.example.expensemanager.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.adapter.TransactionsAdapter
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.models.User
import com.example.expensemanager.ui.activities.AddTransactionActivity
import com.example.expensemanager.utils.*
import com.example.expensemanager.viewmodels.UserViewModel
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: UserViewModel
    private lateinit var user:User
    private lateinit var transactionsAdapter: TransactionsAdapter

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

        setUpRecyclerView()

        InternetConnectivityLiveData(requireContext()).observe(viewLifecycleOwner){
            if(it) getCurrentUser()
        }

        binding.addTransactionFab.setOnClickListener {
            val intent=Intent(requireActivity(), AddTransactionActivity::class.java)
            intent.putExtra(USER,user)
            startActivityForResult(intent, ADD_REQUEST_CODE)
        }

        return binding.root
    }

    private fun setUpRecyclerView() = binding.homeRv.apply {
        transactionsAdapter=TransactionsAdapter()
        layoutManager=LinearLayoutManager(activity)
        adapter=transactionsAdapter
        ItemTouchHelper(getItemTouchHelperCallback()).attachToRecyclerView(this)
        binding.homeSwipeRefresh.setOnRefreshListener {
            transactionsAdapter.notifyDataSetChanged()
            binding.homeSwipeRefresh.isRefreshing=false
        }
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
        transactionsAdapter.transactions = user.transactionList.sortedByDescending {
            it.createdAt
        }.toList()
    }

    private fun getItemTouchHelperCallback()= object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            binding.homePb.visibility=View.VISIBLE
            val position=viewHolder.adapterPosition

            val transaction = transactionsAdapter.transactions[position]
            user.transactionList.remove(transaction)

            transactionsAdapter.transactions = user.transactionList.sortedByDescending {
                it.createdAt
            }.toList()

            if(transaction.transactionType== EXPENSE)
                user.expense-=transaction.amount
            else
                user.income -= transaction.amount
            user.balance=user.income - user.expense

            viewModel.updateUser(user)
            viewModel.updateTask.observe(viewLifecycleOwner){ done ->
                if(done){
                    Toast.makeText(requireContext(),"Deleted!",Toast.LENGTH_SHORT).show()
                    updateUI()
                }else{
                    user.transactionList.add(transaction)
                    if(transaction.transactionType== EXPENSE)
                        user.expense+=transaction.amount
                    else
                        user.income += transaction.amount
                    user.balance=user.income - user.expense
                    updateUI()
                    Toast.makeText(requireContext(),"Failed to Delete!",Toast.LENGTH_LONG).show()
                }
            }
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
