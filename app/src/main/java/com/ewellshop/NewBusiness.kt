package com.ewellshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Constants.TAG
import kotlinx.android.synthetic.main.activity_new_business.*

class NewBusiness : AppCompatActivity() {

    val actions = Actions()
    var parishes = ArrayList<Location>()
    var cities = ArrayList<Location>()

    var selectedParish: Location? = null
    var selectedCity: Location? = null

//    var allowGoingBack = true
    private val userID = actions.getInfo("userID", DataType.INTEGER).toString().toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_business)

        fetchLocations()
        setupLocationPickers()
        setupPhoneFields()
        getExtras()
    }

    private fun getExtras(){
        intent.extras?.let {
//            allowGoingBack = it.getBoolean("allowGoingBack")
        }
    }

    private fun fetchLocations(){
        Internet(this, DOMAIN + "v1/locations", null, true, retry = true){
            for (parish in it.getJSONArray("parishes"))
                parishes.add(Location(parish.getInt("id"), parish.getString("name")))

            for (city in it.getJSONArray("cities"))
                cities.add(Location(city.getInt("id"), city.getString("name"), city.getInt("parish")))
        }
    }

    private fun setupLocationPickers(){
        parishET.setOnClickListener {
            Actions.showAlert(this, "Choose Parish", null, ArrayList<String>().apply {
                parishes.forEach { add(it.name) }
            }){ _, which ->
                // SET PARISH
                selectedParish = parishes[which]
                parishET.setText(parishes[which].name)

                selectedCity = null
                cityET.setText("")
            }
        }

        cityET.setOnClickListener {
            if (selectedParish == null) Actions.showAlert(this, null, "Select your parish first")
            val parish = selectedParish?.id ?: return@setOnClickListener
            val parishCities = cities.filter { it.parent == parish }
            Actions.showAlert(this, "Choose City/Town", null, ArrayList<String>().apply {
                parishCities.forEach { add(it.name) }
            }){ _, which ->
                // SET PARISH
                selectedCity = parishCities[which]
                cityET.setText(parishCities[which].name)
            }
        }
    }

    private fun setupPhoneFields(){
        whatsappET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                whatsappET.removeTextChangedListener(this)
                whatsappET.setText(s.toString().formatNumber(Constants.PHONE_MASK))
                whatsappET.setSelection(whatsappET.text.length)
                whatsappET.addTextChangedListener(this)
            }
        })

        phoneET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                phoneET.removeTextChangedListener(this)
                phoneET.setText(s.toString().formatNumber(Constants.PHONE_MASK))
                phoneET.setSelection(phoneET.text.length)
                phoneET.addTextChangedListener(this)
            }
        })
    }

    fun cont(v: View){
        if (selectedCity == null){
            Actions.showAlert(this, null, "Select your business location")
            return
        }

        if (!agreeCB.isChecked){
            Actions.showAlert(this, null, "You must agree to our terms and " +
                    "privacy policy to continue")
            return
        }

        val name = nameET.text.toString()
        val address = addressET.text.toString()
        val location = selectedCity!!.id
        val email = emailET.text.toString()

        val phone = phoneET.text.toString().numbers()
        val whatsapp = whatsappET.text.toString().numbers()

        if (phone.length < 10 || whatsapp.length < 10){
            val what = if (phone.length == 10) "whatsapp" else "phone"
            Actions.showAlert(this, null, "Provide a valid $what number")
            return
        }

        Internet(this, DOMAIN + "v1/new-business", hashMapOf(
            "name" to name,
            "address" to address,
            "location" to location,
            "email" to email,
            "phone" to "1$phone",
            "whatsapp" to "1$whatsapp",
            "userID" to userID
        ), true){
            actions.saveInfo("employeeID", it.getInt("employeeID"))
            actions.saveInfo("businessID", it.getInt("businessID"))
            actions.saveInfo("employeeState", XBusinessEmployees.MANAGER.value)

            Actions.showAlert(this, "Business created successfully!", "We have successfully " +
                    "created your business, $name! Start listing your store items and add your " +
                    "employees", "Great!", { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
            }, cancellable = false)
        }
    }

    override fun onBackPressed() {
//        if (allowGoingBack) super.onBackPressed()
        Actions.showAlert(this, null, "Cancel business creation at this time?", "Yes", { _, _ ->
            startActivity(Intent(this, MainActivity::class.java))
        })
    }

    fun goBack(v: View){
//        if (allowGoingBack)
            onBackPressed()
//        else {
            // CANCEL BUSINESS REGISTRATION
//            Actions.showAlert(this, null, "You don't want to create your business at this time?",
//                "Yes", {_, _ ->
//                    startActivity(Intent(this, Chooser::class.java).apply {
//                        putExtra("isNewAccount", true)
//                    })
//                }, true)
//        }
    }
}