package edu.cit.capendit.unisell.admin.returns.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.returns.model.AdminReturnResponse

class AdminReturnAdapter(
    private val returns: MutableList<AdminReturnResponse>
) : RecyclerView.Adapter<AdminReturnAdapter.ReturnViewHolder>() {

    class ReturnViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrder: TextView = view.findViewById(R.id.tvReturnOrder)
        val tvVendor: TextView = view.findViewById(R.id.tvReturnVendor)
        val tvProduct: TextView = view.findViewById(R.id.tvReturnProduct)
        val tvReason: TextView = view.findViewById(R.id.tvReturnReason)
        val tvDate: TextView = view.findViewById(R.id.tvReturnDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_return, parent, false)
        return ReturnViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReturnViewHolder, position: Int) {
        val record = returns[position]
        holder.tvOrder.text = "Order #${record.orderId}"
        holder.tvVendor.text = "${record.vendorName} (${record.vendorEmail})"
        holder.tvProduct.text = "${record.productName} x${record.quantity}"
        holder.tvReason.text = record.reason ?: "—"
        holder.tvDate.text = record.returnedAt
    }

    override fun getItemCount(): Int = returns.size

    fun updateData(newReturns: List<AdminReturnResponse>) {
        returns.clear()
        returns.addAll(newReturns)
        notifyDataSetChanged()
    }
}
