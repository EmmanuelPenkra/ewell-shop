package com.ewellshop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ewellshop.helpers.AccountType
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Internet
import com.ewellshop.helpers.PinType
import kotlinx.android.synthetic.main.activity_email.*
import kotlinx.android.synthetic.main.activity_email.subTitleTV
import kotlinx.android.synthetic.main.activity_email.titleTV
import kotlinx.android.synthetic.main.activity_personal.*

class Personal : AppCompatActivity() {

    private var accountType: AccountType? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)

        getExtras()
    }

    private fun getExtras(){
        val bundle = intent.extras ?: return
        accountType = bundle.getSerializable("accountType") as? AccountType
        email = bundle.getString("email")
        when (accountType){
            AccountType.NEW_OWNER -> {
                titleTV.text = "More About You"
                subTitleTV.text = "Provide your legal first name and last name to create your " +
                        "account"
            }
            AccountType.NEW_EMPLOYEE -> {
                titleTV.text = "About Employee"
                subTitleTV.text = "Provide new employee's legal first name and last name below"
            }
            else -> {
                finish()
            }
        }
    }

    fun cont(v: View){
        val firstName = firstNameET.text.toString()
        val lastName = lastNameET.text.toString()

        Internet(this, DOMAIN + "v1/new-user", hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email!!
        ), true){
            // NEW USER CREATED
            when (accountType){
                AccountType.NEW_OWNER -> {
                    // CREATE PIN IF NEW OWNER
                    startActivity(Intent(this, Pin::class.java).apply {
                        putExtra("accountType", accountType)
                        putExtra("userID", it.getInt("userID"))
                        putExtra("pinType", PinType.CREATE)
                    })
                }
                AccountType.NEW_EMPLOYEE -> {
                    // GO BACK IF EMPLOYEE
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra("userID", it.getInt("userID"))
                    })
                    finish()
                }
            }
        }
    }

    fun goBack(v: View){
        onBackPressed()
    }
}