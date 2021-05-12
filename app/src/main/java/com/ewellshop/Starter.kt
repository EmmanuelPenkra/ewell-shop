package com.ewellshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ewellshop.helpers.AccountType

class Starter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)
    }

    fun signIn(v: View){
        startActivity(Intent(this, Email::class.java).apply {
            putExtra("accountType", AccountType.SIGN_IN)
        })
    }

    fun signUp(v: View){
        startActivity(Intent(this, Email::class.java).apply {
            putExtra("accountType", AccountType.NEW_OWNER)
        })
    }
}