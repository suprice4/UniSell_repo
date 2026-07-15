package edu.cit.capendit.unisell.product.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.category.model.CategoryResponse
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.inventory.model.ProductPlatformInventoryRequest
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import edu.cit.capendit.unisell.product.adapter.ProductAdapter
import edu.cit.capendit.unisell.product.model.ProductRequest
import edu.cit.capendit.unisell.product.model.ProductResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class ProductFragment : Fragment(R.layout.fragment_product) {

    private lateinit var btnAddProduct: Button
    private lateinit var tvProductError: TextView
    private lateinit var progressBarProducts: ProgressBar
    private lateinit var rvProducts: RecyclerView

    private val productList = mutableListOf<ProductResponse>()
    private val categoryList = mutableListOf<CategoryResponse>() // fetched independently for the add/edit dialog spinner
    private val platformList = mutableListOf<PlatformResponse>() // fetched independently for the allocation spinner
    private lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddProduct = view.findViewById(R.id.btnAddProduct)
        tvProductError = view.findViewById(R.id.tvProductError)
        progressBarProducts = view.findViewById(R.id.progressBarProducts)
        rvProducts = view.findViewById(R.id.rvProducts)

        productAdapter = ProductAdapter(
            productList,
            platformList,
            onEdit = { showEditProductDialog(it) },
            onDelete = { showDeleteProductConfirm(it) },
            onToggleExpand = { toggleProductPlatforms(it) },
            onAllocate = { productId, platformId, quantity -> allocateStock(productId, platformId, quantity) },
            onRemoveAllocation = { productId, platformId -> removeAllocation(productId, platformId) }
        )
        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.adapter = productAdapter
        rvProducts.isNestedScrollingEnabled = false

        btnAddProduct.setOnClickListener { showAddProductDialog() }

        // Fetch categories and platforms first (needed for dialog + adapter), then products.
        loadCategoriesThenPlatformsThenProducts()
    }

    private fun loadCategoriesThenPlatformsThenProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val catResponse = ApiClient.categoryApi.getCategories()
                if (catResponse.isSuccessful && catResponse.body() != null) {
                    categoryList.clear()
                    categoryList.addAll(catResponse.body()!!)
                }
            } catch (e: Exception) {
                // Non-fatal: product list/dialog will just show "no categories" state if this silently fails
            }

            try {
                val platResponse = ApiClient.platformApi.getPlatforms()
                if (platResponse.isSuccessful && platResponse.body() != null) {
                    platformList.clear()
                    platformList.addAll(platResponse.body()!!)
                }
            } catch (e: Exception) {
                // Non-fatal: allocation spinner will just show empty if this silently fails
            }

            loadProducts()
        }
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

    private fun setProductLoading(loading: Boolean) {
        progressBarProducts.visibility = if (loading) View.VISIBLE else View.GONE
        btnAddProduct.isEnabled = !loading
    }

    private fun showProductError(message: String?) {
        if (message == null) {
            tvProductError.visibility = View.GONE
        } else {
            tvProductError.text = message
            tvProductError.visibility = View.VISIBLE
        }
    }

    private fun loadProducts() {
        showProductError(null)
        setProductLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.productApi.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    productAdapter.updateData(response.body()!!)
                } else {
                    showProductError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showProductError("Network error: ${e.message}")
            } finally {
                setProductLoading(false)
            }
        }
    }

    private fun buildProductDialog(existing: ProductResponse?, onSave: (ProductRequest) -> Unit) {
        if (categoryList.isEmpty()) {
            showProductError("You need at least one category before adding a product")
            return
        }

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_form, null)
        val etName = view.findViewById<EditText>(R.id.etProductName)
        val etSku = view.findViewById<EditText>(R.id.etProductSku)
        val etPrice = view.findViewById<EditText>(R.id.etProductPrice)
        val etQuantity = view.findViewById<EditText>(R.id.etProductQuantity)
        val spinner = view.findViewById<Spinner>(R.id.spinnerProductCategory)
        val dialogError = view.findViewById<TextView>(R.id.tvProductDialogError)

        val categoryNames = categoryList.map { it.name }
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames)

        if (existing != null) {
            etName.setText(existing.name)
            etSku.setText(existing.sku)
            etPrice.setText(existing.price.toString())
            etQuantity.setText(existing.quantity.toString())
            val preselectIndex = categoryList.indexOfFirst { it.id == existing.categoryId }
            if (preselectIndex >= 0) spinner.setSelection(preselectIndex)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Add Product" else "Edit Product")
            .setView(view)
            .setPositiveButton(if (existing == null) "Add" else "Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogError.visibility = View.GONE

                val name = etName.text.toString().trim()
                val sku = etSku.text.toString().trim()
                val priceText = etPrice.text.toString().trim()
                val quantityText = etQuantity.text.toString().trim()
                val lowStock = existing?.lowStockThreshold
                val selectedCategory = categoryList[spinner.selectedItemPosition]

                if (name.isEmpty() || sku.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                    dialogError.text = "All fields except low stock threshold are required"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                val price = priceText.toDoubleOrNull()
                val quantity = quantityText.toIntOrNull()

                if (price == null || price <= 0) {
                    dialogError.text = "Price must be a positive number"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                if (quantity == null || quantity < 0) {
                    dialogError.text = "Quantity must be zero or more"
                    dialogError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                onSave(
                    ProductRequest(
                        name = name,
                        sku = sku,
                        price = price,
                        quantity = quantity,
                        lowStockThreshold = lowStock,
                        categoryId = selectedCategory.id
                    )
                )
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showAddProductDialog() {
        buildProductDialog(existing = null) { request -> createProduct(request) }
    }

    private fun showEditProductDialog(product: ProductResponse) {
        buildProductDialog(existing = product) { request -> updateProduct(product.id, request) }
    }

    private fun createProduct(request: ProductRequest) {
        showProductError(null)
        setProductLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.productApi.createProduct(request)
                if (response.isSuccessful && response.body() != null) {
                    loadProducts()
                } else {
                    showProductError(extractErrorMessage(response))
                    setProductLoading(false)
                }
            } catch (e: Exception) {
                showProductError("Network error: ${e.message}")
                setProductLoading(false)
            }
        }
    }

    private fun updateProduct(id: Long, request: ProductRequest) {
        showProductError(null)
        setProductLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.productApi.updateProduct(id, request)
                if (response.isSuccessful && response.body() != null) {
                    loadProducts()
                } else {
                    showProductError(extractErrorMessage(response))
                    setProductLoading(false)
                }
            } catch (e: Exception) {
                showProductError("Network error: ${e.message}")
                setProductLoading(false)
            }
        }
    }

    private fun showDeleteProductConfirm(product: ProductResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Delete '${product.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteProduct(product.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(id: Long) {
        showProductError(null)
        setProductLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.productApi.deleteProduct(id)
                if (response.isSuccessful) {
                    loadProducts()
                } else {
                    showProductError(extractErrorMessage(response))
                    setProductLoading(false)
                }
            } catch (e: Exception) {
                showProductError("Network error: ${e.message}")
                setProductLoading(false)
            }
        }
    }

    private fun toggleProductPlatforms(product: ProductResponse) {
        if (productAdapter.isExpanded(product.id)) {
            productAdapter.collapseProduct()
        } else {
            loadAllocationsForProduct(product.id)
        }
    }

    private fun loadAllocationsForProduct(productId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.inventoryApi.getAllocations(productId)
                if (response.isSuccessful && response.body() != null) {
                    productAdapter.expandProduct(productId, response.body()!!)
                } else {
                    showProductError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showProductError("Network error: ${e.message}")
            }
        }
    }

    private fun allocateStock(productId: Long, platformId: Long, quantity: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.inventoryApi.allocateStock(
                    productId,
                    ProductPlatformInventoryRequest(platformId, quantity)
                )
                if (response.isSuccessful) {
                    loadAllocationsForProduct(productId)
                } else {
                    productAdapter.setInventoryError(productId, extractErrorMessage(response))
                }
            } catch (e: Exception) {
                productAdapter.setInventoryError(productId, "Network error: ${e.message}")
            }
        }
    }

    private fun removeAllocation(productId: Long, platformId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.inventoryApi.deleteAllocation(productId, platformId)
                if (response.isSuccessful) {
                    loadAllocationsForProduct(productId)
                } else {
                    productAdapter.setInventoryError(productId, extractErrorMessage(response))
                }
            } catch (e: Exception) {
                productAdapter.setInventoryError(productId, "Network error: ${e.message}")
            }
        }
    }
}