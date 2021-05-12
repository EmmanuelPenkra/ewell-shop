package com.ewellshop

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import kotlinx.android.synthetic.main.activity_pin.*

class Pin : AppCompatActivity() {

    val actions = Actions()
    var oldPin = String()
    var newPin = String()

    var userID: Int? = null
    var pinType: PinType? = null
    var accountType: AccountType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        pin1.addTextChangedListener {
            textChanged(it.toString(), pin2)
        }
        pin2.addTextChangedListener {
            textChanged(it.toString(), pin3)
        }
        pin3.addTextChangedListener {
            textChanged(it.toString(), pin4)
        }
        pin4.addTextChangedListener {
            textChanged(it.toString())
        }
        pin2.setOnKeyListener { _, keyCode, _ ->
            clearFields(keyCode)
            false
        }
        pin3.setOnKeyListener { _, keyCode, _ ->
            clearFields(keyCode)
            false
        }
        pin4.setOnKeyListener { _, keyCode, _ ->
            clearFields(keyCode)
            false
        }

        getExtras()
    }

    fun clearFields(keyCode: Int){
        if (keyCode != KeyEvent.KEYCODE_DEL) return

        pin1.setText("")
        pin2.setText("")
        pin3.setText("")
        pin4.setText("")

        pin1.requestFocus()
        newPin = ""
    }

    private fun textChanged(text: String, next: EditText? = null){
        if (text.isEmpty()) return

        newPin += text
        next?.requestFocus()
        if (next == null) cont()
    }

    private fun cont(){
        when (pinType){
            PinType.CREATE -> {
                startActivity(Intent(this, Pin::class.java).apply {
                    putExtra("pinType", PinType.CONFIRM)
                    putExtra("oldPin", newPin)
                    putExtra("accountType", accountType)
                    putExtra("userID", userID)
                })
            }
            else -> {
                // CHECK OLD PIN WITH NEW PIN
                if (oldPin != newPin) return

                actions.saveInfo("pin", newPin)

                when (pinType){
                    PinType.CONFIRM -> {
                        // ADD PIN TO DATABASE
                        Internet(this, DOMAIN + "v1/save-pin", hashMapOf(
                            "userID" to userID!!,
                            "pin" to newPin
                        ), true){
                            checkAccount()
                        }
                    }
                    PinType.LOGIN -> {
                        checkAccount()
                    }
                    PinType.AUTHORIZE -> {
                        // TODO: ALLOW BACK IN
                    }
                }
            }
        }
    }

    private fun checkAccount(){
        actions.saveInfo("userID", userID!!)
        // CHECK IF CREATING A NEW BUSINESS OR JUST SIGNING IN
        when (accountType){
            AccountType.NEW_OWNER -> {
                // MOVE TO CREATING BUSINESS
                startActivity(Intent(this, NewBusiness::class.java).apply {
                    putExtra("allowGoingBack", false)
                })
            }
            AccountType.SIGN_IN -> {
                startActivity(Intent(this, Chooser::class.java))
            }
        }
    }

    private fun getExtras(){
        intent.extras?.let {
            pinType = it.getSerializable("pinType") as? PinType
            accountType = it.getSerializable("accountType") as? AccountType
            userID = it.getInt("userID", 0)

            if (pinType == PinType.AUTHORIZE || (pinType == PinType.CREATE && accountType == AccountType.NEW_OWNER)){
                backB.isVisible = false
                infoTV.isVisible = true
            }else {
                backB.isVisible = true
                infoTV.isVisible = false
            }

            if (pinType != PinType.CREATE)
                oldPin = it.getString("oldPin").toString()

            when (pinType){
                PinType.CREATE -> {
                    titleTV.text = "Create a PIN"
                    infoTV.text = "For security reasons, you will be required to provide your PIN " +
                    "everytime you open the app."
                }
                PinType.CONFIRM -> {
                    titleTV.text = "Confirm your PIN"
                }
                PinType.AUTHORIZE, PinType.LOGIN -> {
                    titleTV.text = "Input your PIN"

                    // TODO: CONFIRMING TO GET INTO APP IF AUTHORIZING
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!(pinType == PinType.AUTHORIZE || (pinType == PinType.CREATE && accountType ==
                    AccountType.NEW_OWNER)))
            super.onBackPressed()
    }

    fun goBack(v: View){
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        clearFields(KeyEvent.KEYCODE_DEL)
    }
}