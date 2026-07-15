package edu.cit.capendit.unisell.order.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.order.adapter.OrderAdapter
import edu.cit.capendit.unisell.order.model.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var tvOrderListError: TextView
    private lateinit var progressBarOrders: ProgressBar
    private lateinit var rvOrders: RecyclerView

    private val orderList = mutableListOf<OrderResponse>()
    private lateinit var orderAdapter: OrderAdapter
    private var expandedOrderId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvOrderListError = view.findViewById(R.id.tvOrderListError)
        progressBarOrders = view.findViewById(R.id.progressBarOrders)
        rvOrders = view.findViewById(R.id.rvOrders)

        orderAdapter = OrderAdapter(
            orderList,
            onToggleExpand = { loadItemsForOrder(it) },
            onAdvanceStatus = { advanceStatus(it) },
            onMarkPaymentReceived = { markPaymentReceived(it) },
            onMarkUncollected = { markUncollected(it) },
            onProcessReturn = { orderId, itemIds, reason -> processReturn(orderId, itemIds, reason) }
        )
        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = orderAdapter
        rvOrders.isNestedScrollingEnabled = false

        loadOrders()
    }

    private fun extractErrorMessage(response: Response<*>): String {
        val raw = response.errorBody()?.string()
        if (raw.isNullOrBlank()) return "Something went wrong (${response.code()})"
        return try {
            JSONObject(raw).optString("message", raw)
        } catch (e: Exception) {
            raw
        }
    }

    private fun showListError(message: String?) {
        tvOrderListError.visibility = if (message == null) View.GONE else View.VISIBLE
        tvOrderListError.text = message ?: ""
    }

    private fun loadOrders() {
        showListError(null)
        progressBarOrders.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.orderApi.getOrders()
                if (response.isSuccessful && response.body() != null) {
                    orderAdapter.updateData(response.body()!!)
                } else {
                    showListError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showListError("Network error: ${e.message}")
            } finally {
                progressBarOrders.visibility = View.GONE
            }
        }
    }

    private fun loadItemsForOrder(order: OrderResponse) {
        if (expandedOrderId == order.id) {
            expandedOrderId = null
            return
        }
        expandedOrderId = order.id
        orderAdapter.setItemsLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.orderApi.getOrderItems(order.id)
                if (response.isSuccessful && response.body() != null) {
                    orderAdapter.setItemsForOrder(order.id, response.body()!!)
                } else {
                    orderAdapter.setActionError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                orderAdapter.setActionError("Network error: ${e.message}")
            } finally {
                orderAdapter.setItemsLoading(false)
            }
        }
    }

    private fun advanceStatus(order: OrderResponse) {
        val next = mapOf("PENDING" to "PROCESSING", "PROCESSING" to "SHIPPED", "SHIPPED" to "DELIVERED")[order.status] ?: return
        runAction(order.id) { ApiClient.orderApi.updateStatus(order.id, StatusUpdateRequest(next)) }
    }

    private fun markPaymentReceived(order: OrderResponse) {
        runAction(order.id) { ApiClient.orderApi.updatePaymentStatus(order.id, PaymentStatusUpdateRequest("RECEIVED")) }
    }

    private fun markUncollected(order: OrderResponse) {
        runAction(order.id) { ApiClient.orderApi.markUncollected(order.id, ShipmentStatusUpdateRequest("UNCOLLECTED")) }
    }

    private fun processReturn(orderId: Long, itemIds: List<Long>, reason: String) {
        if (itemIds.isEmpty()) {
            orderAdapter.setActionError("Select at least one item to return.")
            return
        }
        runAction(orderId) { ApiClient.orderApi.processReturn(orderId, ReturnRequest(itemIds, reason)) }
    }

    private fun runAction(orderId: Long, call: suspend () -> Response<OrderResponse>) {
        orderAdapter.setActionError(null)
        orderAdapter.setActionLoading(orderId)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = call()
                if (response.isSuccessful && response.body() != null) {
                    orderAdapter.replaceOrder(response.body()!!)
                } else {
                    orderAdapter.setActionError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                orderAdapter.setActionError("Network error: ${e.message}")
            } finally {
                orderAdapter.setActionLoading(null)
            }
        }
    }
}