package com.ewellshop

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Constants.TAG
import kotlinx.android.synthetic.main.activity_email.*

class Email : AppCompatActivity() {

    private val NEW_EMPLOYEE = 100

    private var accountType: AccountType? = null
    private var userID: Int? = null
    private var email: String? = null
    private var pin = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        getExtras()
    }

    private fun getExtras(){
        val bundle = intent.extras ?: return
        accountType = bundle.getSerializable("accountType") as? AccountType
        when (accountType){
            AccountType.NEW_OWNER -> {
                titleTV.text = "Create Your Account"
                subTitleTV.text = "Sign up as a business owner! Type in your personal email below"
            }
            AccountType.NEW_EMPLOYEE -> {
                titleTV.text = "Add an Employee"
                subTitleTV.text = "Type new employee's email below"
            }
            AccountType.SIGN_IN -> {
                titleTV.text = "Welcome back"
                subTitleTV.text = "Provide your email below to get into access to your business account"
            }
            else -> {
                finish()
            }
        }
    }

    private fun showConfirmEmailPopup(code: String){
        // INPUT FIELD
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            hint = "6-digit Code"
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            setBackgroundColor(resources.getColor(R.color.background))
            typeface = ResourcesCompat.getFont(this@Email, R.font.pt_sans)
        }

        AlertDialog.Builder(this).apply {
            setTitle("Verify your email")
            setMessage("We have sent a 6-digit code to the email you provided. " +
                    "Provide it below\n")
            setView(editText)
            setPositiveButton("Continue") { _, _ ->
                checkConfirmationCode(code, editText.text.toString())
            }
            setNegativeButton("Cancel", null)
        }.create().run {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun checkConfirmationCode(code: String, input: String) {
        if (code != input) {
            // SHOW ERROR MESSAGE AND SHOW CONFIRMATION CODE AGAIN
            Actions.showAlert(
                this,
                "Error",
                "The code you entered is incorrect",
                "Try again",
                { _, _ ->
                    showConfirmEmailPopup(code)
                })
            return
        }

        // CONFIRMED!
        userID?.let { userID ->
            when (accountType) {
                AccountType.NEW_EMPLOYEE -> {
                    // ADD EMPLOYEE
                    newEmployee(userID)
                }
                else -> {
                    // CREATE PIN IF DON'T HAVE PIN
                    // CONFIRM PIN IF HAVE PIN
                    startActivity(Intent(this, Pin::class.java).apply {
                        putExtra("accountType", accountType)
                        putExtra("userID", userID)
                        putExtra("pinType", if (pin.isEmpty()) PinType.CREATE else PinType.LOGIN)
                        putExtra("oldPin", pin)
                    })
                }
            }
            return@checkConfirmationCode
        }

        // CREATE NEW OWNER OR EMPLOYEE
        // RETURN IF EMPLOYEE
        startActivityForResult(Intent(this, Personal::class.java).apply {
            putExtra("accountType", accountType)
            putExtra("email", email)
        }, NEW_EMPLOYEE)
    }

    private fun newEmployee(userID: Int){
        val businessID = Actions().getInfo("businessID", DataType.INTEGER)!!
        Internet(this, DOMAIN + "v1/new-employee", hashMapOf(
            "userID" to userID,
            "businessID" to businessID
        ), true){
            // EMPLOYEE ADDED, GO BACK
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) data?.let {
            when (requestCode){
                NEW_EMPLOYEE -> newEmployee(it.getIntExtra("userID", 0))
            }
        }
    }

    fun cont(v: View){
        val email = emailET.text.toString()

        Internet(this, DOMAIN + "v1/email", hashMapOf(
            "email" to email,
            "accountType" to accountType!!.name
        ), true){
            if (it.has("userID")) {
                userID = it.getInt("userID")
                pin = it.getString("pin")
            }

            this.email = it.getString("email")

            showConfirmEmailPopup(it.getString("code"))
        }
    }

    fun goBack(v: View){
        onBackPressed()
    }
}