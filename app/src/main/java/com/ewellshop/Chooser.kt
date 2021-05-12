package com.ewellshop

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ewellshop.collections.SettingsAdapter
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.TAG
import kotlinx.android.synthetic.main.activity_chooser.*

class Chooser : AppCompatActivity() {

    val actions = Actions()

    private var settingsAdapter: SettingsAdapter? = null
    var settings = ArrayList<Setting>()
    var businessIDs = ArrayList<Int>()

//    var isNewAccount = true
    private val userID = actions.getInfo("userID", DataType.INTEGER).toString().toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        settingsAdapter = SettingsAdapter(this, settings)
        settingsLV.adapter = settingsAdapter

        settingsLV.setOnItemClickListener { parent, view, position, id ->
            val setting = settings[position]
            if (setting.id == -1){
                // CREATE BUSINESS
//                if (isNewAccount) onBackPressed()
//                else {
                    // START PROCEDURE
                    startActivity(Intent(this, Pin::class.java).apply {
                        putExtra("accountType", AccountType.NEW_OWNER)
                        putExtra("userID", userID)
                        putExtra("pinType", PinType.LOGIN)
                        putExtra("oldPin", actions.getInfo("pin", DataType.STRING).toString())
                    })
//                }
                return@setOnItemClickListener
            }

            // SWITCH TO SELECTED ACCOUNT
            actions.saveInfo("employeeID", businessIDs[position])
            actions.saveInfo("businessID", setting.id)
            actions.saveInfo("employeeState", XBusinessEmployees.valueOf(setting.sub))

            Actions.showAlert(this, null, "Welcome back to ${setting.main}!", "Great!", { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
            }, cancellable = false)
        }

        getExtras()
        getAccounts()
    }

    private fun getExtras(){
        intent.extras?.let {
//            isNewAccount = it.getBoolean("allowGoingBack")
        }
    }

    private fun getAccounts(){
        Internet(this, Constants.DOMAIN + "v1/accounts", hashMapOf(
            "userID" to userID
        ), true, {
            // ERROR HAPPENED SO SHOW MESSAGE
        }){
            for (account in it.getJSONArray("accounts")){
                businessIDs.add(account.getInt("businessID"))
                settings.add(Setting(account.getInt("id"), account.getString("businessName"),
                    XBusinessEmployees.values()[account.getInt("state")].name.formatEnum()
                        .capitalizeWords()))
            }

//            settings.add(
//                Setting(-1, "Create your business", "List your store products on Ewell " +
//                    "Shop today", true)
//            )

            settingsAdapter?.notifyDataSetChanged()
        }
    }

    fun goBack(v: View){
        onBackPressed()
    }
}