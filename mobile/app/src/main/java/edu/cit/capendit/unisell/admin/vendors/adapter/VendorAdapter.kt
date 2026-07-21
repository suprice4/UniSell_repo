package edu.cit.capendit.unisell.admin.vendors.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.vendors.model.VendorResponse

class VendorAdapter(
    private val vendors: MutableList<VendorResponse>,
    private val onToggle: (VendorResponse) -> Unit
) : RecyclerView.Adapter<VendorAdapter.VendorViewHolder>() {

    class VendorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvVendorName)
        val tvEmail: TextView = view.findViewById(R.id.tvVendorEmail)
        val tvStatus: TextView = view.findViewById(R.id.tvVendorStatus)
        val btnToggle: Button = view.findViewById(R.id.btnVendorToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_vendor, parent, false)
        return VendorViewHolder(view)
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val vendor = vendors[position]
        holder.tvName.text = vendor.name
        holder.tvEmail.text = vendor.email
        holder.tvStatus.text = if (vendor.enabled) "Active" else "Deactivated"
        holder.btnToggle.text = if (vendor.enabled) "Deactivate" else "Activate"
        holder.btnToggle.setOnClickListener { onToggle(vendor) }
    }

    override fun getItemCount(): Int = vendors.size

    fun updateData(newVendors: List<VendorResponse>) {
        vendors.clear()
        vendors.addAll(newVendors)
        notifyDataSetChanged()
    }
}
