package edu.cit.capendit.unisell.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.cit.capendit.unisell.R

class VendorDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_dashboard)
        // Categories, Platforms, Products, and Orders are each self-contained Fragments,
        // declared statically in activity_vendor_dashboard.xml via FragmentContainerView.
    }
}