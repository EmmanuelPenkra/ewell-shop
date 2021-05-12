package com.ewellshop

import android.app.Application
import android.content.Context

class EwellShop : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
    }
}