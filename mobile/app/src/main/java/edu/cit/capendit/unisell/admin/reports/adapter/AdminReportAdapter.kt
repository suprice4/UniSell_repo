package edu.cit.capendit.unisell.admin.reports.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.reports.model.AdminVendorReportResponse

class AdminReportAdapter(
    private val reports: MutableList<AdminVendorReportResponse>
) : RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVendor: TextView = view.findViewById(R.id.tvReportVendor)
        val tvOrders: TextView = view.findViewById(R.id.tvReportOrders)
        val tvInventory: TextView = view.findViewById(R.id.tvReportInventory)
        val tvBreakdown: TextView = view.findViewById(R.id.tvReportBreakdown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.tvVendor.text = "${report.vendorName} (${report.vendorEmail})"
        holder.tvOrders.text = "Total orders: ${report.totalOrders}"
        holder.tvInventory.text = "Total inventory: ${report.totalInventory}"
        holder.tvBreakdown.text = "Pending: ${report.paymentStatusBreakdown["PENDING"] ?: 0}  " +
            "Received: ${report.paymentStatusBreakdown["RECEIVED"] ?: 0}  " +
            "Refunded: ${report.paymentStatusBreakdown["REFUNDED"] ?: 0}"
    }

    override fun getItemCount(): Int = reports.size

    fun updateData(newReports: List<AdminVendorReportResponse>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }
}
