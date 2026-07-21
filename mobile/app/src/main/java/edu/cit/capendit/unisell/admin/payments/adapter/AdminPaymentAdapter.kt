package edu.cit.capendit.unisell.admin.payments.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.payments.model.AdminPaymentResponse

class AdminPaymentAdapter(
    private val payments: MutableList<AdminPaymentResponse>
) : RecyclerView.Adapter<AdminPaymentAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrder: TextView = view.findViewById(R.id.tvPaymentOrder)
        val tvVendor: TextView = view.findViewById(R.id.tvPaymentVendor)
        val tvPlatform: TextView = view.findViewById(R.id.tvPaymentPlatform)
        val tvAmount: TextView = view.findViewById(R.id.tvPaymentAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvPaymentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.tvOrder.text = "Order #${payment.orderId}"
        holder.tvVendor.text = "${payment.vendorName} (${payment.vendorEmail})"
        holder.tvPlatform.text = payment.platformName
        holder.tvAmount.text = "₱${"%.2f".format(payment.totalAmount)}"
        holder.tvStatus.text = payment.paymentStatus
        holder.tvStatus.setTextColor(
            if (payment.paymentStatus == "PAID") Color.parseColor("#2E7D32") else Color.parseColor("#C0392B")
        )
    }

    override fun getItemCount(): Int = payments.size

    fun updateData(newPayments: List<AdminPaymentResponse>) {
        payments.clear()
        payments.addAll(newPayments)
        notifyDataSetChanged()
    }
}
