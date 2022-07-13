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
import com.example.expensemanager.models.User
import com.example.expensemanager.utils.EXPENSE
import com.example.expensemanager.utils.InternetConnectivityLiveData
import com.example.expensemanager.utils.getUserViewModel
import com.example.expensemanager.viewmodels.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment() {
    private lateinit var _binding:FragmentStatisticsBinding
    private val binding get() = _binding
    private lateinit var viewModel: UserViewModel
    private lateinit var user: User
    private val colorList = ArrayList<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentStatisticsBinding.inflate(layoutInflater)

        viewModel= getUserViewModel(this,requireActivity().application)

        colorList.add(ContextCompat.getColor(requireContext(),R.color.theme1))
        colorList.add(ContextCompat.getColor(requireContext(),R.color.theme2))

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

        lineChart()
        pieChart()
        barChart(binding.incomeBarChart,"Income",true)
        barChart(binding.expenseBarChart,"Expense",false)
    }

    private fun lineChart() {
        val incomeList=ArrayList<Entry>()
        val expenseList=ArrayList<Entry>()
        var countI=1
        var countE=1

        for(i in user.transactionList.indices){
            val transaction = user.transactionList[i]
            if(transaction.transactionType!= EXPENSE) {
                incomeList.add(Entry(countI.toFloat(),transaction.amount.toFloat()))
                countI++
            }else{
                expenseList.add(Entry(countE.toFloat(),transaction.amount.toFloat()))
                countE++
            }
        }

        val incomeDataSet = LineDataSet(incomeList,"Income")
        incomeDataSet.color= ContextCompat.getColor(requireContext(),R.color.theme1)
        incomeDataSet.valueTextColor=Color.BLACK
        incomeDataSet.valueTextSize=12F
        incomeDataSet.setDrawCircles(false)

        val expenseDataSet = LineDataSet(expenseList,"Expense")
        expenseDataSet.color= ContextCompat.getColor(requireContext(),R.color.theme2)
        expenseDataSet.valueTextColor=Color.BLACK
        expenseDataSet.valueTextSize=12F
        expenseDataSet.setDrawCircles(false)

        val list = ArrayList<ILineDataSet>()
        list.add(incomeDataSet)
        list.add(expenseDataSet)

        val lineData=LineData(list)

        binding.lineChart.data=lineData
        binding.lineChart.description.text="Transactions"
        binding.lineChart.animateY(1000)
    }

    private fun pieChart() {
        val records = ArrayList<PieEntry>()
        records.add(PieEntry(user.income.toFloat(),"Income"))
        records.add(PieEntry(user.expense.toFloat(),"Expense"))

        val pieDataSet = PieDataSet(records,"Transaction Report")

        pieDataSet.colors=colorList.toMutableList()
        pieDataSet.valueTextColor=Color.WHITE
        pieDataSet.valueTextSize=14F

        val pieData=PieData(pieDataSet)
        binding.pieChart.data=pieData
        binding.pieChart.description.isEnabled=false
        binding.pieChart.centerText="Transaction"
        binding.pieChart.animate()
        binding.pieChart.invalidate()
    }

    private fun barChart(bar: BarChart,type:String,isIncome:Boolean) {
        val list:ArrayList<BarEntry> = ArrayList()
        var count=1
        if(isIncome){
            for(i in user.transactionList.indices){
                val transaction = user.transactionList[i]
                if(transaction.transactionType!= EXPENSE){
                    list.add(BarEntry(count.toFloat(),transaction.amount.toFloat()))
                    count++
                }
            }
        }else{
            for(i in user.transactionList.indices){
                val transaction = user.transactionList[i]
                if(transaction.transactionType == EXPENSE){
                    list.add(BarEntry(count.toFloat(),transaction.amount.toFloat()))
                    count++
                }
            }
        }

        val barDataSet=BarDataSet(list,type)
        barDataSet.colors=ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor=Color.BLACK
        barDataSet.valueTextSize=14F

        val barData=BarData(barDataSet)
        bar.setFitBars(true)
        bar.data=barData
        bar.description.text="$type report"
        bar.animateY(1000)
    }
}