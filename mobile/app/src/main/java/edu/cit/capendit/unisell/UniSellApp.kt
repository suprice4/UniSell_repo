package edu.cit.capendit.unisell

import android.app.Application

class UniSellApp : Application() {
    override fun onCreate() {
        super.onCreate()
        edu.cit.capendit.unisell.api.ApiClient.init(applicationContext)
    }
}