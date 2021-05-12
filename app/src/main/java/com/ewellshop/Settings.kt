package com.ewellshop

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ewellshop.collections.SettingsAdapter
import com.ewellshop.helpers.Actions
import com.ewellshop.helpers.Setting
import kotlinx.android.synthetic.main.fragment_settings.view.*


class Settings : Fragment() {

    val actions = Actions()
    var contextX: Context? = null

    private var settingsAdapter: SettingsAdapter? = null
    var settings = arrayListOf (
        Setting(1, "Business Info", "Direct link, business contact, address, etc", true),
        Setting(2, "Completed Orders", "See orders recently completed"),
        Setting(3, "My Account", "See your personal information"),
        Setting(4, "FAQs", "Find answers to your most common questions"),
        Setting(5, "Employees", "Manage workers and their permissions", true),
        Setting(6, "Earnings", "See all payments made to your business"),
        Setting(8, "Super Items", "Best selling items in your inventory"),
        Setting(9, "My Business", "Make changes to your business"),
        Setting(10, "Get in Touch", "Contact us with your issues and suggestions"),
        Setting(7, "Logout", "Sign out from this device", true)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

        contextX = requireActivity()

        val settingsLV = v.settingsLV

        settingsAdapter = SettingsAdapter(contextX!!, settings)
        settingsLV.adapter = settingsAdapter

        settingsLV.setOnItemClickListener { parent, view, position, id ->
            when (settings[position].id){
                1 -> {}
                2 -> {}
                3 -> {}
                4 -> {}
                5 -> {}
                6 -> {}
                8 -> {}
                9 -> {}
                7 -> {
                    Actions.showAlert(contextX!!, null, "Are you sure you want to sign out?",
                        "Yes", { _, _ ->
                            actions.removeAll()
                            startActivity(Intent(contextX!!, MainActivity::class.java))
                        }, true)
                }
                10 -> {}
            }
        }

        return v
    }
}