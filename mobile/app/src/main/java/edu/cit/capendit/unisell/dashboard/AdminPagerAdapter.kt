package edu.cit.capendit.unisell.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.cit.capendit.unisell.admin.vendors.ui.VendorsFragment
import edu.cit.capendit.unisell.admin.returns.ui.ReturnsFragment

class AdminPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    val tabTitles = listOf("Vendors", "Returns")

    override fun getItemCount(): Int = tabTitles.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> VendorsFragment()
            1 -> ReturnsFragment()
            else -> throw IllegalArgumentException("Unknown admin tab position: $position")
        }
    }
}
