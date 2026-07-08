package edu.cit.capendit.unisell.core

import android.app.Application
import edu.cit.capendit.unisell.core.ApiClient

class UniSellApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(applicationContext)
    }
}