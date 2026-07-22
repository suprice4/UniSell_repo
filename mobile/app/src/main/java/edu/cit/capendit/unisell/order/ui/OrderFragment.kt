package edu.cit.capendit.unisell.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.dashboard.DashboardViewModel
import edu.cit.capendit.unisell.order.adapter.OrderAdapter
import edu.cit.capendit.unisell.order.model.*
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import edu.cit.capendit.unisell.product.model.ProductResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var btnCreateOrder: Button
    private lateinit var tvOrderListError: TextView
    private lateinit var progressBarOrders: ProgressBar
    private lateinit var rvOrders: RecyclerView

    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private val orderList = mutableListOf<OrderResponse>()
    private val platformList = mutableListOf<PlatformResponse>()
    private val productList = mutableListOf<ProductResponse>()
    private lateinit var orderAdapter: OrderAdapter
    private var expandedOrderId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCreateOrder = view.findViewById(R.id.btnCreateOrder)
        tvOrderListError = view.findViewById(R.id.tvOrderListError)
        progressBarOrders = view.findViewById(R.id.progressBarOrders)
        rvOrders = view.findViewById(R.id.rvOrders)

        orderAdapter = OrderAdapter(
            orderList,
            onToggleExpand = { loadItemsForOrder(it) },
            onAdvanceStatus = { advanceStatus(it) },
            onMarkPaymentReceived = { markPaymentReceived(it) },
            onMarkUncollected = { markUncollected(it) },
            onProcessReturn = { orderId, itemIds, reason -> processReturn(orderId, itemIds, reason) },
            onSaveShipmentDetails = { orderId, tracking, courier -> saveShipmentDetails(orderId, tracking, courier) },
            onDeleteOrder = { deleteOrder(it) }
        )
        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = orderAdapter
        rvOrders.isNestedScrollingEnabled = false

        btnCreateOrder.setOnClickListener { showCreateOrderDialog() }

        dashboardViewModel.platforms.observe(viewLifecycleOwner) {
            platformList.clear()
            platformList.addAll(it)
        }
        dashboardViewModel.loadPlatformsIfNeeded()
        loadProductsForOrderForm()

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

    private fun saveShipmentDetails(orderId: Long, trackingNumber: String, courierName: String) {
        if (trackingNumber.isEmpty()) {
            orderAdapter.setActionError("Tracking number is required.")
            return
        }
        if (courierName.isEmpty()) {
            orderAdapter.setActionError("Courier name is required.")
            return
        }
        runAction(orderId) {
            ApiClient.orderApi.updateShipmentDetails(orderId, ShipmentDetailsRequest(trackingNumber, courierName))
        }
    }

    private fun deleteOrder(orderId: Long) {
        orderAdapter.setActionError(null)
        orderAdapter.setActionLoading(orderId)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.orderApi.deleteOrder(orderId)
                if (response.isSuccessful) {
                    loadOrders()
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

    private fun loadProductsForOrderForm() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.productApi.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    productList.clear()
                    productList.addAll(response.body()!!)
                }
            } catch (e: Exception) {
                // Silent: the create-order dialog will just show an empty product list and the
                // user can retry by reopening it once products are available.
            }
        }
    }

    private fun showCreateOrderDialog() {
        if (platformList.isEmpty()) {
            showListError("You need at least one platform before creating an order")
            return
        }
        if (productList.isEmpty()) {
            showListError("You need at least one product before creating an order")
            return
        }

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_form, null)
        val spinnerPlatform = view.findViewById<Spinner>(R.id.spinnerOrderPlatform)
        val etCustomerName = view.findViewById<EditText>(R.id.etOrderCustomerName)
        val etCustomerAddress = view.findViewById<EditText>(R.id.etOrderCustomerAddress)
        val llItemRows = view.findViewById<LinearLayout>(R.id.llOrderItemRows)
        val btnAddItemRow = view.findViewById<Button>(R.id.btnAddOrderItemRow)
        val dialogError = view.findViewById<TextView>(R.id.tvOrderDialogError)

        spinnerPlatform.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, platformList.map { it.name }
        )

        val productNames = productList.map { "${it.name} (${it.sku})" }
        fun addItemRow() {
            val row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_order_form_row, llItemRows, false)
            val spinnerProduct = row.findViewById<Spinner>(R.id.spinnerOrderRowProduct)
            val etQuantity = row.findViewById<EditText>(R.id.etOrderRowQuantity)
            val btnRemove = row.findViewById<Button>(R.id.btnRemoveOrderRow)
            spinnerProduct.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, productNames)
            btnRemove.setOnClickListener {
                if (llItemRows.childCount > 1) llItemRows.removeView(row)
            }
            llItemRows.addView(row)
        }
        addItemRow()
        btnAddItemRow.setOnClickListener { addItemRow() }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Create Order")
            .setView(view)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogError.visibility = View.GONE

                val customerName = etCustomerName.text.toString().trim()
                val customerAddress = etCustomerAddress.text.toString().trim()
                if (customerName.isEmpty()) {
                    dialogError.text = "Customer name is required"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                if (customerAddress.isEmpty()) {
                    dialogError.text = "Customer address is required"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                val items = mutableListOf<OrderItemRequest>()
                for (i in 0 until llItemRows.childCount) {
                    val row = llItemRows.getChildAt(i)
                    val spinnerProduct = row.findViewById<Spinner>(R.id.spinnerOrderRowProduct)
                    val etQuantity = row.findViewById<EditText>(R.id.etOrderRowQuantity)
                    val quantity = etQuantity.text.toString().trim().toIntOrNull()
                    if (quantity != null && quantity > 0) {
                        val product = productList[spinnerProduct.selectedItemPosition]
                        items.add(OrderItemRequest(product.id, quantity))
                    }
                }
                if (items.isEmpty()) {
                    dialogError.text = "Add at least one product with a quantity"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                val platform = platformList[spinnerPlatform.selectedItemPosition]
                createOrder(
                    OrderRequest(platform.id, customerName, customerAddress, items)
                )
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun createOrder(request: OrderRequest) {
        showListError(null)
        progressBarOrders.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.orderApi.createOrder(request)
                if (response.isSuccessful && response.body() != null) {
                    loadOrders()
                } else {
                    showListError(extractErrorMessage(response))
                    progressBarOrders.visibility = View.GONE
                }
            } catch (e: Exception) {
                showListError("Network error: ${e.message}")
                progressBarOrders.visibility = View.GONE
            }
        }
    }
}