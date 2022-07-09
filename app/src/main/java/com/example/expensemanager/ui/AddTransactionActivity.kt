package com.example.expensemanager.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.expensemanager.databinding.ActivityAddTransactionBinding
import com.example.expensemanager.models.Transaction
import com.example.expensemanager.models.User
import com.example.expensemanager.utils.*
import com.example.expensemanager.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_add_transaction.*
import nl.bryanderidder.themedtogglebuttongroup.SelectAnimation

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var transactionType:String
    private var typeSelected=false
    private lateinit var user: User
    private lateinit var viewModel: UserViewModel

    companion object{
        const val MIN_TRANSACTION_LENGTH=2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= getUserViewModel(this,application)

        binding.typeGroup.selectAnimation=SelectAnimation.CIRCULAR_REVEAL
        registerListenersForEnablingAddBtn()

        binding.addTransToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.addTransBtn.setOnClickListener {
            addTransaction()
        }

        user= intent.getSerializableExtra(USER) as User
    }

    private fun addTransaction() {
        binding.addTransPb.visibility= View.VISIBLE
        val transaction=Transaction(
            binding.allocateForEditText.text.toString(),
            binding.amountEditText.text.toString().toInt(),
            transactionType,
            System.currentTimeMillis()
        )

        if(transaction.transactionType == EXPENSE)
            user.expense+=transaction.amount.toLong()
        else
            user.income+=transaction.amount.toLong()

        user.balance=user.income-user.expense
        user.transactionList.add(transaction)

        viewModel.updateUser(user)
        viewModel.updateTask.observe(this){
            if(it==true){
                hidePb()
                val resultData=Intent()
                resultData.putExtra(UPDATED_USER,user)
                setResult(Activity.RESULT_OK,resultData)
                finish()
                Toast.makeText(this, "Done!", Toast.LENGTH_LONG).show()
            }else {
                hidePb()
                Toast.makeText(this, "Failed to add transaction!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hidePb() {
        binding.addTransPb.visibility= View.GONE
    }

    private fun registerListenersForEnablingAddBtn() {
        binding.typeGroup.setOnSelectListener {btn->
            transactionType=btn.text
            typeSelected=btn.isSelected
            binding.addTransBtn.isEnabled = shouldEnabledAddBtn()
        }
        etChangeListener(binding.amountEditText)
        etChangeListener(binding.allocateForEditText)
    }

    private fun etChangeListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.addTransBtn.isEnabled=shouldEnabledAddBtn()
            }
        })
    }

    private fun shouldEnabledAddBtn(): Boolean {
        if(!typeSelected)
            return false
        if(binding.allocateForEditText.text.isBlank() || binding.amountEditText.text.isBlank()
            || binding.amountEditText.text.length < MIN_TRANSACTION_LENGTH)
            return false
        return true
    }
}