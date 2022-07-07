package com.example.expensemanager.models

data class User(
    val uid:String="",
    val email:String="",
    val displayName:String="",
    val imageUrl:String="",
    var balance:Int = 0,
    var income:Int = 0,
    var expense:Int = 0,
    val transactionList: ArrayList<Transaction> = ArrayList()
)