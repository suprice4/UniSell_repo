package edu.cit.capendit.unisell.category.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.category.adapter.CategoryAdapter
import edu.cit.capendit.unisell.category.model.CategoryRequest
import edu.cit.capendit.unisell.category.model.CategoryResponse
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.dashboard.DashboardViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var etNewCategory: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvCategories: RecyclerView

    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNewCategory = view.findViewById(R.id.etNewCategory)
        btnAddCategory = view.findViewById(R.id.btnAddCategory)
        tvError = view.findViewById(R.id.tvError)
        progressBar = view.findViewById(R.id.progressBar)
        rvCategories = view.findViewById(R.id.rvCategories)

        categoryAdapter = CategoryAdapter(
            mutableListOf(),
            onEdit = { showEditCategoryDialog(it) },
            onDelete = { showDeleteCategoryConfirm(it) }
        )
        rvCategories.layoutManager = LinearLayoutManager(requireContext())
        rvCategories.adapter = categoryAdapter
        rvCategories.isNestedScrollingEnabled = false

        btnAddCategory.setOnClickListener { addCategory() }

        dashboardViewModel.categories.observe(viewLifecycleOwner) { categoryAdapter.updateData(it) }
        dashboardViewModel.loadCategoriesIfNeeded()
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
        dashboardViewModel.refreshCategories { success, error ->
            if (!success) showError(error)
            setLoading(false)
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
        viewLifecycleOwner.lifecycleScope.launch {
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
        val input = EditText(requireContext())
        input.setText(category.name)

        AlertDialog.Builder(requireContext())
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
        viewLifecycleOwner.lifecycleScope.launch {
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
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Delete '${category.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteCategory(category.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCategory(id: Long) {
        showError(null)
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
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