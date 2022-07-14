package com.example.expensemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.databinding.TransactionItem2Binding
import com.example.expensemanager.models.Transaction
import com.example.expensemanager.utils.TimeAgo

class TransactionsAdapter2 : RecyclerView.Adapter<TransactionsAdapter2.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TransactionItem2Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model=transactions[position]
        holder.binding.apply {
            cardAmount.text=model.amount.toString()+" Rs."
            cardName.text=model.name
            cardTime.text=TimeAgo.getTimeAgo(model.createdAt)
        }
    }

    override fun getItemCount(): Int = transactions.size

    inner class ViewHolder(val binding:TransactionItem2Binding):RecyclerView.ViewHolder(binding.root)
    private val differCallback=object : DiffUtil.ItemCallback<Transaction>(){
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem.createdAt==newItem.createdAt
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem==newItem
    }
    private val differ= AsyncListDiffer(this,differCallback)
    var transactions:List<Transaction>
        get() = differ.currentList
        set(value){differ.submitList(value)}
}