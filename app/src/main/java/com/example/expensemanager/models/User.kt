package com.example.expensemanager.models

import java.io.Serializable

data class User(
    val uid:String="",
    val email:String="",
    val displayName:String="",
    val imageUrl:String="",
    var balance:Long = 0,
    var income:Long = 0,
    var expense:Long = 0,
    val transactionList: ArrayList<Transaction> = ArrayList()
):Serializable