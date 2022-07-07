package com.example.expensemanager.models

import android.view.SurfaceControl

data class Transaction(val name:String="",val amount:Int=0,val transactionType:String="",val createdAt:Long=0L)