package com.example.expensemanager.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.R
import com.example.expensemanager.databinding.TransactionItemBinding
import com.example.expensemanager.models.Transaction
import com.example.expensemanager.utils.EXPENSE
import com.example.expensemanager.utils.TimeAgo

class TransactionsAdapter(val context: Context) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TransactionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = transactions[position]
        holder.binding.apply {
            transNameTv.text=model.name
            transAmountTv.text=model.amount.toString()+" Rs."
            transCreatedAtTv.text = TimeAgo.getTimeAgo(model.createdAt)
            transTypeImage.setImageResource(if(model.transactionType== EXPENSE) R.drawable.dot_bg_2 else R.drawable.dot_bg)
            transAmountTv.setTextColor(
                if(model.transactionType== EXPENSE) ContextCompat.getColor(context,R.color.theme2)
                else ContextCompat.getColor(context,R.color.theme1)
            )
        }
    }

    override fun getItemCount(): Int = transactions.size

    inner class ViewHolder(val binding:TransactionItemBinding):RecyclerView.ViewHolder(binding.root)

    private val differCallback=object : DiffUtil.ItemCallback<Transaction>(){
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem.createdAt==newItem.createdAt
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem==newItem
    }
    private val differ=AsyncListDiffer(this,differCallback)
    var transactions:List<Transaction>
        get() = differ.currentList
        set(value){differ.submitList(value)}
}