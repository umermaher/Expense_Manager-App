package com.example.expensemanager.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.expensemanager.R
import com.example.expensemanager.databinding.FragmentStatisticsBinding
import com.example.expensemanager.models.Transaction
import com.example.expensemanager.models.User
import com.example.expensemanager.utils.EXPENSE
import com.example.expensemanager.utils.InternetConnectivityLiveData
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment() {
    private lateinit var _binding:FragmentStatisticsBinding
    private val binding get() = _binding
    private lateinit var viewModel: UserViewModel
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentStatisticsBinding.inflate(layoutInflater)

        viewModel= getUserViewModel(this,requireActivity().application)

        InternetConnectivityLiveData(requireContext()).observe(viewLifecycleOwner){
            if(it){
                getCurrentUser()
            }
        }

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getCurrentUser() {
        binding.statisticsPb.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            user= viewModel.getCurrentUser()
            withContext(Dispatchers.Main){
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.statisticsPb.visibility=View.GONE

        barChart()
        pieChart()
    }

    private fun pieChart() {
        val records = ArrayList<PieEntry>()
        records.add(PieEntry(user.income.toFloat(),"Income"))
        records.add(PieEntry(user.expense.toFloat(),"Expense"))

        val pieDataSet = PieDataSet(records,"Transaction Report")

        val colorList = ArrayList<Int>()
        colorList.add(ContextCompat.getColor(requireContext(),R.color.theme1))
        colorList.add(ContextCompat.getColor(requireContext(),R.color.theme2))
        pieDataSet.colors=colorList.toMutableList()
        pieDataSet.valueTextColor=Color.WHITE
        pieDataSet.valueTextSize=16F

        val pieData=PieData(pieDataSet)
        binding.picChart.data=pieData
        binding.picChart.description.isEnabled=true
        binding.picChart.centerText="Transaction"
        binding.picChart.animate()
        binding.picChart.invalidate()
    }

    private fun barChart() {
        var ex=2010
        val incomeList:ArrayList<BarEntry> = ArrayList()
        for(i in user.transactionList.indices){
//            if(user.transactionList[i].transactionType != EXPENSE){
            val calendar=Calendar.getInstance()
            calendar.timeInMillis=user.transactionList[i].createdAt
            incomeList.add(BarEntry(ex.toFloat(),user.transactionList[i].amount.toFloat()))
//            }
            ex++
        }

        val barDataSet=BarDataSet(incomeList,"Income")
        barDataSet.colors=ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor=Color.BLACK
        barDataSet.valueTextSize=16F

        val barData=BarData(barDataSet)
        binding.transactionBarChart.setFitBars(true)
        binding.transactionBarChart.data=barData
        binding.transactionBarChart.description.text="Income report"
        binding.transactionBarChart.animateY(1000)
    }
}