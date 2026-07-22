package edu.cit.capendit.unisell.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import edu.cit.capendit.unisell.inventory.model.ProductPlatformInventoryResponse
import edu.cit.capendit.unisell.product.model.ProductResponse
import edu.cit.capendit.unisell.core.util.isLowStock

class ProductAdapter(
    private val products: MutableList<ProductResponse>,
    private val platformList: List<PlatformResponse>, // live reference held by the Activity
    private val onEdit: (ProductResponse) -> Unit,
    private val onDelete: (ProductResponse) -> Unit,
    private val onToggleExpand: (ProductResponse) -> Unit,
    private val onAllocate: (productId: Long, platformId: Long, quantity: Int) -> Unit,
    private val onRemoveAllocation: (productId: Long, platformId: Long) -> Unit,
    private val onUpdateAllocation: (productId: Long, platformId: Long, quantity: Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var expandedProductId: Long? = null
    private val inventoryByProduct = mutableMapOf<Long, List<ProductPlatformInventoryResponse>>()
    private val inventoryErrorByProduct = mutableMapOf<Long, String?>()
    private var editingAllocationPlatformId: Long? = null

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvLowStock: TextView = view.findViewById(R.id.tvProductLowStock)
        val tvDetails: TextView = view.findViewById(R.id.tvProductDetails)
        val btnEdit: Button = view.findViewById(R.id.btnEditProduct)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteProduct)
        val btnTogglePlatforms: Button = view.findViewById(R.id.btnTogglePlatforms)
        val panel: LinearLayout = view.findViewById(R.id.llInventoryPanel)
        val llAllocationRows: LinearLayout = view.findViewById(R.id.llAllocationRows)
        val tvInventoryError: TextView = view.findViewById(R.id.tvInventoryError)
        val spinnerAllocationPlatform: Spinner = view.findViewById(R.id.spinnerAllocationPlatform)
        val etAllocationQuantity: EditText = view.findViewById(R.id.etAllocationQuantity)
        val btnAllocate: Button = view.findViewById(R.id.btnAllocate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name
        holder.tvLowStock.visibility =
            if (isLowStock(product.quantity, product.lowStockThreshold)) View.VISIBLE else View.GONE
        holder.tvDetails.text =
            "SKU: ${product.sku} | ₱${product.price} | Qty: ${product.quantity} | ${product.categoryName}"
        holder.btnEdit.setOnClickListener { onEdit(product) }
        holder.btnDelete.setOnClickListener { onDelete(product) }
        holder.btnTogglePlatforms.setOnClickListener { onToggleExpand(product) }

        val isExpanded = product.id == expandedProductId
        holder.panel.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.btnTogglePlatforms.text = if (isExpanded) "Hide Platforms" else "Platforms"

        if (!isExpanded) return

        // Populate allocation rows
        holder.llAllocationRows.removeAllViews()
        val allocations = inventoryByProduct[product.id] ?: emptyList()
        if (allocations.isEmpty()) {
            val emptyText = TextView(holder.itemView.context)
            emptyText.text = "No allocations yet"
            emptyText.textSize = 14f
            holder.llAllocationRows.addView(emptyText)
        } else {
            for (allocation in allocations) {
                val row = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_inventory_allocation_row, holder.llAllocationRows, false)

                val llDisplay = row.findViewById<LinearLayout>(R.id.llAllocationDisplay)
                val llEdit = row.findViewById<LinearLayout>(R.id.llAllocationEdit)
                val tvLabel = row.findViewById<TextView>(R.id.tvAllocationLabel)
                val tvLowStockBadge = row.findViewById<TextView>(R.id.tvAllocationLowStock)
                val tvEditPlatform = row.findViewById<TextView>(R.id.tvAllocationEditPlatform)
                val etEditQuantity = row.findViewById<EditText>(R.id.etAllocationEditQuantity)

                tvLabel.text = "${allocation.platformName}: ${allocation.allocatedQuantity}"
                tvLowStockBadge.visibility =
                    if (isLowStock(allocation.allocatedQuantity, product.lowStockThreshold)) View.VISIBLE else View.GONE

                val isEditingThisRow = editingAllocationPlatformId == allocation.platformId
                llDisplay.visibility = if (isEditingThisRow) View.GONE else View.VISIBLE
                llEdit.visibility = if (isEditingThisRow) View.VISIBLE else View.GONE

                row.findViewById<Button>(R.id.btnEditAllocation).setOnClickListener {
                    editingAllocationPlatformId = allocation.platformId
                    notifyDataSetChanged()
                }
                row.findViewById<Button>(R.id.btnRemoveAllocation).setOnClickListener {
                    onRemoveAllocation(product.id, allocation.platformId)
                }

                tvEditPlatform.text = allocation.platformName
                etEditQuantity.setText(allocation.allocatedQuantity.toString())
                row.findViewById<Button>(R.id.btnCancelAllocation).setOnClickListener {
                    editingAllocationPlatformId = null
                    notifyDataSetChanged()
                }
                row.findViewById<Button>(R.id.btnSaveAllocation).setOnClickListener {
                    val quantity = etEditQuantity.text.toString().trim().toIntOrNull()
                    if (quantity == null || quantity < 0) {
                        setInventoryError(product.id, "Quantity must be zero or more")
                        return@setOnClickListener
                    }
                    editingAllocationPlatformId = null
                    onUpdateAllocation(product.id, allocation.platformId, quantity)
                }

                holder.llAllocationRows.addView(row)
            }
        }

        // Populate platform spinner
        val platformNames = platformList.map { it.name }
        holder.spinnerAllocationPlatform.adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_dropdown_item,
            platformNames
        )

        val errorMessage = inventoryErrorByProduct[product.id]
        holder.tvInventoryError.visibility = if (errorMessage == null) View.GONE else View.VISIBLE
        holder.tvInventoryError.text = errorMessage ?: ""

        holder.btnAllocate.setOnClickListener {
            if (platformList.isEmpty()) {
                setInventoryError(product.id, "No platforms exist yet")
                return@setOnClickListener
            }
            val quantityText = holder.etAllocationQuantity.text.toString().trim()
            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity < 0) {
                setInventoryError(product.id, "Quantity must be zero or more")
                return@setOnClickListener
            }
            val selectedPlatform = platformList[holder.spinnerAllocationPlatform.selectedItemPosition]
            holder.etAllocationQuantity.text.clear()
            onAllocate(product.id, selectedPlatform.id, quantity)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<ProductResponse>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun isExpanded(productId: Long): Boolean = productId == expandedProductId

    fun expandProduct(productId: Long, allocations: List<ProductPlatformInventoryResponse>) {
        expandedProductId = productId
        inventoryByProduct[productId] = allocations
        inventoryErrorByProduct[productId] = null
        notifyDataSetChanged()
    }

    fun collapseProduct() {
        expandedProductId = null
        notifyDataSetChanged()
    }

    fun setInventoryError(productId: Long, message: String?) {
        inventoryErrorByProduct[productId] = message
        notifyDataSetChanged()
    }
}