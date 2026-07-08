package edu.cit.capendit.unisell.category

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.core.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class VendorDashboardActivity : AppCompatActivity() {

    private lateinit var etNewCategory: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvCategories: RecyclerView

    private val categoryList = mutableListOf<CategoryResponse>()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_dashboard)

        etNewCategory = findViewById(R.id.etNewCategory)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        rvCategories = findViewById(R.id.rvCategories)

        adapter = CategoryAdapter(
            categoryList,
            onEdit = { showEditDialog(it) },
            onDelete = { showDeleteConfirm(it) }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter

        btnAddCategory.setOnClickListener { addCategory() }

        loadCategories()
    }

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

    private fun extractErrorMessage(response: Response<*>): String {
        val raw = response.errorBody()?.string()
        if (raw.isNullOrBlank()) return "Something went wrong (${response.code()})"
        return try {
            JSONObject(raw).optString("message", raw)
        } catch (e: Exception) {
            raw
        }
    }

    private fun loadCategories() {
        showError(null)
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = ApiClient.categoryApi.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!)
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

    private fun showEditDialog(category: CategoryResponse) {
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

    private fun showDeleteConfirm(category: CategoryResponse) {
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
}