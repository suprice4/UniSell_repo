package edu.cit.capendit.unisell.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.category.adapter.CategoryAdapter
import edu.cit.capendit.unisell.category.model.CategoryRequest
import edu.cit.capendit.unisell.category.model.CategoryResponse
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.product.adapter.ProductAdapter
import edu.cit.capendit.unisell.product.model.ProductRequest
import edu.cit.capendit.unisell.product.model.ProductResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import edu.cit.capendit.unisell.platform.adapter.PlatformAdapter
import edu.cit.capendit.unisell.platform.model.PlatformRequest
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import edu.cit.capendit.unisell.inventory.model.ProductPlatformInventoryRequest

class VendorDashboardActivity : AppCompatActivity() {

    // ---- Category views ----
    private lateinit var etNewCategory: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvCategories: RecyclerView

    private val categoryList = mutableListOf<CategoryResponse>()
    private lateinit var categoryAdapter: CategoryAdapter

    // ---- Product views ----
    private lateinit var btnAddProduct: Button
    private lateinit var tvProductError: TextView
    private lateinit var progressBarProducts: ProgressBar
    private lateinit var rvProducts: RecyclerView

    private val productList = mutableListOf<ProductResponse>()
    private lateinit var productAdapter: ProductAdapter

    // ---- Platform views ----
    private lateinit var etNewPlatform: EditText
    private lateinit var btnAddPlatform: Button
    private lateinit var tvPlatformError: TextView
    private lateinit var progressBarPlatforms: ProgressBar
    private lateinit var rvPlatforms: RecyclerView

    private val platformList = mutableListOf<PlatformResponse>()
    private lateinit var platformAdapter: PlatformAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_dashboard)

        // Category views
        etNewCategory = findViewById(R.id.etNewCategory)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        rvCategories = findViewById(R.id.rvCategories)

        categoryAdapter = CategoryAdapter(
            categoryList,
            onEdit = { showEditCategoryDialog(it) },
            onDelete = { showDeleteCategoryConfirm(it) }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = categoryAdapter

        btnAddCategory.setOnClickListener { addCategory() }

        // Product views
        btnAddProduct = findViewById(R.id.btnAddProduct)
        tvProductError = findViewById(R.id.tvProductError)
        progressBarProducts = findViewById(R.id.progressBarProducts)
        rvProducts = findViewById(R.id.rvProducts)

        productAdapter = ProductAdapter(
            productList,
            platformList,
            onEdit = { showEditProductDialog(it) },
            onDelete = { showDeleteProductConfirm(it) },
            onToggleExpand = { toggleProductPlatforms(it) },
            onAllocate = { productId, platformId, quantity -> allocateStock(productId, platformId, quantity) },
            onRemoveAllocation = { productId, platformId -> removeAllocation(productId, platformId) }
        )
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productAdapter

        btnAddProduct.setOnClickListener { showAddProductDialog() }

        // Platform views
        etNewPlatform = findViewById(R.id.etNewPlatform)
        btnAddPlatform = findViewById(R.id.btnAddPlatform)
        tvPlatformError = findViewById(R.id.tvPlatformError)
        progressBarPlatforms = findViewById(R.id.progressBarPlatforms)
        rvPlatforms = findViewById(R.id.rvPlatforms)

        platformAdapter = PlatformAdapter(
            platformList,
            onEdit = { showEditPlatformDialog(it) },
            onDelete = { showDeletePlatformConfirm(it) }
        )
        rvPlatforms.layoutManager = LinearLayoutManager(this)
        rvPlatforms.adapter = platformAdapter

        btnAddPlatform.setOnClickListener { addPlatform() }

        loadCategories()
        loadProducts()
        loadPlatforms()
    }

    // ============ Shared helpers ============

    private fun extractErrorMessage(response: Response<*>): String {
        val raw = response.errorBody()?.string()
        if (raw.isNullOrBlank()) return "Something went wrong (${response.code()})"
        return try {
            JSONObject(raw).optString("message", raw)
        } catch (e: Exception) {
            raw
        }
    }

    // ============ Category section ============

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnAddCategory.isEnabled = !loading
        etNewCategory.isEnabled = !loading
    }

    private fun showError(message: String?) {
        if (message == null) {
            tvError.visibility = View.GONE
        } else {
            tvError.text = message
            tvError.visibility = View.VISIBLE
        }
    }

    private fun loadCategories() {
        showError(null)
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.categoryApi.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    categoryAdapter.updateData(response.body()!!)
                } else {
                    showError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun addCategory() {
        val name = etNewCategory.text.toString().trim()
        if (name.isEmpty()) {
            showError("Category name cannot be empty")
            return
        }
        showError(null)
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.categoryApi.createCategory(CategoryRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    etNewCategory.text.clear()
                    loadCategories()
                } else {
                    showError(extractErrorMessage(response))
                    setLoading(false)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                setLoading(false)
            }
        }
    }

    private fun showEditCategoryDialog(category: CategoryResponse) {
        val input = EditText(this)
        input.setText(category.name)

        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                updateCategory(category.id, newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateCategory(id: Long, name: String) {
        if (name.isEmpty()) {
            showError("Category name cannot be empty")
            return
        }
        showError(null)
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.categoryApi.updateCategory(id, CategoryRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    loadCategories()
                } else {
                    showError(extractErrorMessage(response))
                    setLoading(false)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                setLoading(false)
            }
        }
    }

    private fun showDeleteCategoryConfirm(category: CategoryResponse) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Delete '${category.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteCategory(category.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCategory(id: Long) {
        showError(null)
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.categoryApi.deleteCategory(id)
                if (response.isSuccessful) {
                    loadCategories()
                } else {
                    showError(extractErrorMessage(response))
                    setLoading(false)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                setLoading(false)
            }
        }
    }

    // ============ Product section ============

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
        lifecycleScope.launch {
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

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null)
        val etName = view.findViewById<EditText>(R.id.etProductName)
        val etSku = view.findViewById<EditText>(R.id.etProductSku)
        val etPrice = view.findViewById<EditText>(R.id.etProductPrice)
        val etQuantity = view.findViewById<EditText>(R.id.etProductQuantity)
        val spinner = view.findViewById<Spinner>(R.id.spinnerProductCategory)
        val dialogError = view.findViewById<TextView>(R.id.tvProductDialogError)

        val categoryNames = categoryList.map { it.name }
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryNames)

        if (existing != null) {
            etName.setText(existing.name)
            etSku.setText(existing.sku)
            etPrice.setText(existing.price.toString())
            etQuantity.setText(existing.quantity.toString())
            val preselectIndex = categoryList.indexOfFirst { it.id == existing.categoryId }
            if (preselectIndex >= 0) spinner.setSelection(preselectIndex)
        }

        val dialog = AlertDialog.Builder(this)
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
        lifecycleScope.launch {
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
        lifecycleScope.launch {
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
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Delete '${product.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteProduct(product.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(id: Long) {
        showProductError(null)
        setProductLoading(true)
        lifecycleScope.launch {
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
    // ============ Platform section ============

    private fun setPlatformLoading(loading: Boolean) {
        progressBarPlatforms.visibility = if (loading) View.VISIBLE else View.GONE
        btnAddPlatform.isEnabled = !loading
        etNewPlatform.isEnabled = !loading
    }

    private fun showPlatformError(message: String?) {
        if (message == null) {
            tvPlatformError.visibility = View.GONE
        } else {
            tvPlatformError.text = message
            tvPlatformError.visibility = View.VISIBLE
        }
    }

    private fun loadPlatforms() {
        showPlatformError(null)
        setPlatformLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.getPlatforms()
                if (response.isSuccessful && response.body() != null) {
                    platformAdapter.updateData(response.body()!!)
                } else {
                    showPlatformError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
            } finally {
                setPlatformLoading(false)
            }
        }
    }

    private fun addPlatform() {
        val name = etNewPlatform.text.toString().trim()
        if (name.isEmpty()) {
            showPlatformError("Platform name cannot be empty")
            return
        }
        showPlatformError(null)
        setPlatformLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.createPlatform(PlatformRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    etNewPlatform.text.clear()
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }

    private fun showEditPlatformDialog(platform: PlatformResponse) {
        val input = EditText(this)
        input.setText(platform.name)

        AlertDialog.Builder(this)
            .setTitle("Edit Platform")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                updatePlatform(platform.id, newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePlatform(id: Long, name: String) {
        if (name.isEmpty()) {
            showPlatformError("Platform name cannot be empty")
            return
        }
        showPlatformError(null)
        setPlatformLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.updatePlatform(id, PlatformRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }

    private fun showDeletePlatformConfirm(platform: PlatformResponse) {
        AlertDialog.Builder(this)
            .setTitle("Delete Platform")
            .setMessage("Delete '${platform.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deletePlatform(platform.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePlatform(id: Long) {
        showPlatformError(null)
        setPlatformLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.deletePlatform(id)
                if (response.isSuccessful) {
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }
    // ============ Inventory allocation section ============

    private fun toggleProductPlatforms(product: ProductResponse) {
        if (productAdapter.isExpanded(product.id)) {
            productAdapter.collapseProduct()
        } else {
            loadAllocationsForProduct(product.id)
        }
    }

    private fun loadAllocationsForProduct(productId: Long) {
        lifecycleScope.launch {
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
        lifecycleScope.launch {
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
        lifecycleScope.launch {
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