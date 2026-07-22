package edu.cit.capendit.unisell.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.order.model.OrderItemResponse
import edu.cit.capendit.unisell.order.model.OrderResponse

class OrderAdapter(
    private val orders: MutableList<OrderResponse>,
    private val onToggleExpand: (OrderResponse) -> Unit,
    private val onAdvanceStatus: (OrderResponse) -> Unit,
    private val onMarkPaymentReceived: (OrderResponse) -> Unit,
    private val onMarkUncollected: (OrderResponse) -> Unit,
    private val onProcessReturn: (orderId: Long, selectedItemIds: List<Long>, reason: String) -> Unit,
    private val onSaveShipmentDetails: (orderId: Long, trackingNumber: String, courierName: String) -> Unit,
    private val onDeleteOrder: (orderId: Long) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // Matches web's NEXT_STATUS map exactly — RETURNED is reached only via the return flow, never via advance.
    private val nextStatus = mapOf(
        "PENDING" to "PROCESSING",
        "PROCESSING" to "SHIPPED",
        "SHIPPED" to "DELIVERED"
    )

    private var expandedOrderId: Long? = null
    private val itemsByOrder = mutableMapOf<Long, List<OrderItemResponse>>()
    private var itemsLoading = false
    private val selectedReturnItemIds = mutableSetOf<Long>()
    private val returnReasonByOrder = mutableMapOf<Long, String>()
    private val shipmentTrackingByOrder = mutableMapOf<Long, String>()
    private val shipmentCourierByOrder = mutableMapOf<Long, String>()
    private var confirmDeleteOrderId: Long? = null
    private var actionLoadingOrderId: Long? = null
    private var actionError: String? = null

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSummary: TextView = view.findViewById(R.id.tvOrderSummary)
        val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvSubStatus: TextView = view.findViewById(R.id.tvOrderSubStatus)
        val panel: LinearLayout = view.findViewById(R.id.llOrderPanel)
        val tvCustomerInfo: TextView = view.findViewById(R.id.tvOrderCustomerInfo)
        val tvItemsLoading: TextView = view.findViewById(R.id.tvItemsLoading)
        val llItems: LinearLayout = view.findViewById(R.id.llOrderItems)
        val tvActionError: TextView = view.findViewById(R.id.tvOrderActionError)
        val btnAdvance: Button = view.findViewById(R.id.btnAdvanceStatus)
        val btnPayment: Button = view.findViewById(R.id.btnMarkPaymentReceived)
        val btnUncollected: Button = view.findViewById(R.id.btnMarkUncollected)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteOrder)
        val llDeleteConfirm: LinearLayout = view.findViewById(R.id.llDeleteConfirm)
        val btnConfirmDelete: Button = view.findViewById(R.id.btnConfirmDelete)
        val btnCancelDelete: Button = view.findViewById(R.id.btnCancelDelete)
        val llShipmentDetails: LinearLayout = view.findViewById(R.id.llShipmentDetails)
        val etCourierName: EditText = view.findViewById(R.id.etCourierName)
        val etTrackingNumber: EditText = view.findViewById(R.id.etTrackingNumber)
        val btnSaveShipmentDetails: Button = view.findViewById(R.id.btnSaveShipmentDetails)
        val etReason: EditText = view.findViewById(R.id.etReturnReason)
        val btnReturn: Button = view.findViewById(R.id.btnProcessReturn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvSummary.text = "Order #${order.id} — ${order.platformName} — ₱${order.totalAmount}"
        holder.tvStatus.text = order.status
        holder.tvSubStatus.text = "${order.paymentStatus} · ${order.shipmentStatus}"

        holder.itemView.setOnClickListener {
            if (expandedOrderId == order.id) {
                expandedOrderId = null
            } else {
                selectedReturnItemIds.clear()
                expandedOrderId = order.id
                onToggleExpand(order)
            }
            notifyDataSetChanged()
        }

        val isExpanded = order.id == expandedOrderId
        holder.panel.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (!isExpanded) return

        holder.tvItemsLoading.visibility = if (itemsLoading) View.VISIBLE else View.GONE

        holder.llItems.removeAllViews()
        val items = itemsByOrder[order.id] ?: emptyList()
        for (item in items) {
            val row = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_order_item, holder.llItems, false)
            val cb = row.findViewById<CheckBox>(R.id.cbReturnItem)
            val label = row.findViewById<TextView>(R.id.tvOrderItemLabel)
            label.text = "${item.productName} × ${item.quantity} @ ₱${item.priceAtTimeOfOrder}"
            cb.visibility = if (order.status != "RETURNED") View.VISIBLE else View.GONE
            cb.isChecked = selectedReturnItemIds.contains(item.id)
            cb.setOnClickListener {
                if (cb.isChecked) selectedReturnItemIds.add(item.id) else selectedReturnItemIds.remove(item.id)
            }
            holder.llItems.addView(row)
        }

        val hasCustomerInfo = !order.customerName.isNullOrBlank() || !order.customerAddress.isNullOrBlank()
        holder.tvCustomerInfo.visibility = if (hasCustomerInfo) View.VISIBLE else View.GONE
        if (hasCustomerInfo) {
            holder.tvCustomerInfo.text = listOfNotNull(
                order.customerName?.takeIf { it.isNotBlank() }?.let { "Customer: $it" },
                order.customerAddress?.takeIf { it.isNotBlank() }?.let { "Address: $it" }
            ).joinToString("\n")
        }

        holder.tvActionError.visibility = if (actionError == null) View.GONE else View.VISIBLE
        holder.tvActionError.text = actionError ?: ""

        val loadingThisOrder = actionLoadingOrderId == order.id

        val next = nextStatus[order.status]
        holder.btnAdvance.visibility = if (next != null) View.VISIBLE else View.GONE
        holder.btnAdvance.isEnabled = !loadingThisOrder && !(next == "SHIPPED" &&
            (shipmentTrackingByOrder[order.id] ?: order.trackingNumber ?: "").isBlank()) &&
            !(next == "SHIPPED" && (shipmentCourierByOrder[order.id] ?: order.courierName ?: "").isBlank())
        holder.btnAdvance.text = if (loadingThisOrder) "Updating..." else "Mark as $next"
        holder.btnAdvance.setOnClickListener { onAdvanceStatus(order) }

        holder.btnPayment.visibility = if (order.paymentStatus == "PENDING") View.VISIBLE else View.GONE
        holder.btnPayment.isEnabled = !loadingThisOrder
        holder.btnPayment.setOnClickListener { onMarkPaymentReceived(order) }

        holder.btnUncollected.visibility = if (order.status != "RETURNED") View.VISIBLE else View.GONE
        holder.btnUncollected.isEnabled = !loadingThisOrder
        holder.btnUncollected.setOnClickListener { onMarkUncollected(order) }

        holder.btnDelete.visibility = if (order.status == "PENDING") View.VISIBLE else View.GONE
        holder.btnDelete.isEnabled = !loadingThisOrder
        holder.btnDelete.setOnClickListener {
            confirmDeleteOrderId = order.id
            notifyDataSetChanged()
        }

        val confirmingDelete = confirmDeleteOrderId == order.id
        holder.llDeleteConfirm.visibility = if (confirmingDelete) View.VISIBLE else View.GONE
        holder.btnConfirmDelete.isEnabled = !loadingThisOrder
        holder.btnConfirmDelete.setOnClickListener {
            confirmDeleteOrderId = null
            onDeleteOrder(order.id)
        }
        holder.btnCancelDelete.setOnClickListener {
            confirmDeleteOrderId = null
            notifyDataSetChanged()
        }

        val showShipmentDetails = order.status == "PROCESSING" || order.status == "SHIPPED"
        holder.llShipmentDetails.visibility = if (showShipmentDetails) View.VISIBLE else View.GONE
        if (showShipmentDetails) {
            holder.etCourierName.setText(shipmentCourierByOrder[order.id] ?: order.courierName ?: "")
            holder.etTrackingNumber.setText(shipmentTrackingByOrder[order.id] ?: order.trackingNumber ?: "")
            holder.etCourierName.setOnFocusChangeListener { _, _ ->
                shipmentCourierByOrder[order.id] = holder.etCourierName.text.toString()
            }
            holder.etTrackingNumber.setOnFocusChangeListener { _, _ ->
                shipmentTrackingByOrder[order.id] = holder.etTrackingNumber.text.toString()
            }
            holder.btnSaveShipmentDetails.visibility = if (order.status == "SHIPPED") View.VISIBLE else View.GONE
            holder.btnSaveShipmentDetails.isEnabled = !loadingThisOrder
            holder.btnSaveShipmentDetails.setOnClickListener {
                onSaveShipmentDetails(
                    order.id,
                    holder.etTrackingNumber.text.toString().trim(),
                    holder.etCourierName.text.toString().trim()
                )
            }
        }

        val showReturn = order.status != "RETURNED"
        holder.etReason.visibility = if (showReturn) View.VISIBLE else View.GONE
        holder.btnReturn.visibility = if (showReturn) View.VISIBLE else View.GONE
        holder.etReason.setText(returnReasonByOrder[order.id] ?: "")
        holder.etReason.setOnFocusChangeListener { _, _ ->
            returnReasonByOrder[order.id] = holder.etReason.text.toString()
        }
        holder.btnReturn.isEnabled = !loadingThisOrder
        holder.btnReturn.setOnClickListener {
            val reason = holder.etReason.text.toString()
            onProcessReturn(order.id, selectedReturnItemIds.toList(), reason)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<OrderResponse>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    fun setItemsLoading(loading: Boolean) {
        itemsLoading = loading
        notifyDataSetChanged()
    }

    fun setItemsForOrder(orderId: Long, items: List<OrderItemResponse>) {
        itemsByOrder[orderId] = items
        notifyDataSetChanged()
    }

    fun setActionLoading(orderId: Long?) {
        actionLoadingOrderId = orderId
        notifyDataSetChanged()
    }

    fun setActionError(message: String?) {
        actionError = message
        notifyDataSetChanged()
    }

    fun replaceOrder(updated: OrderResponse) {
        val idx = orders.indexOfFirst { it.id == updated.id }
        if (idx >= 0) orders[idx] = updated
        selectedReturnItemIds.clear()
        notifyDataSetChanged()
    }
}