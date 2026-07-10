import { useState, useEffect } from "react";
import api from "../api/axios";

function VendorDashboard() {
  const [categories, setCategories] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const [newName, setNewName] = useState("");
  const [addLoading, setAddLoading] = useState(false);
  const [addError, setAddError] = useState("");

  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState("");
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState("");

  const [deletingId, setDeletingId] = useState(null);

  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [productListError, setProductListError] = useState("");

  const [newProductName, setNewProductName] = useState("");
  const [newProductSku, setNewProductSku] = useState("");
  const [newProductPrice, setNewProductPrice] = useState("");
  const [newProductQuantity, setNewProductQuantity] = useState("");
  const [newProductCategoryId, setNewProductCategoryId] = useState("");
  const [addProductLoading, setAddProductLoading] = useState(false);
  const [addProductError, setAddProductError] = useState("");

  const [deletingProductId, setDeletingProductId] = useState(null);

  const [editingProductId, setEditingProductId] = useState(null);
  const [editProductName, setEditProductName] = useState("");
  const [editProductSku, setEditProductSku] = useState("");
  const [editProductPrice, setEditProductPrice] = useState("");
  const [editProductQuantity, setEditProductQuantity] = useState("");
  const [editProductCategoryId, setEditProductCategoryId] = useState("");
  const [editProductLoading, setEditProductLoading] = useState(false);
  const [editProductError, setEditProductError] = useState("");

  const getErrorMessage = (err, fallback) => {
    const data = err.response?.data;
    return typeof data === "string" ? data : data?.message || fallback;
  };

  const fetchCategories = async () => {
    setListError("");
    setLoadingList(true);
    try {
      const res = await api.get("/categories");
      setCategories(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load categories."));
    } finally {
      setLoadingList(false);
    }
  };

  const fetchProducts = async () => {
    setProductListError("");
    setLoadingProducts(true);
    try {
      const res = await api.get("/products");
      setProducts(res.data);
    } catch (err) {
      setProductListError(getErrorMessage(err, "Failed to load products."));
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchCategories();
    fetchProducts();
  }, []);

  const handleAddProduct = async (e) => {
    e.preventDefault();
    setAddProductError("");
    setAddProductLoading(true);

    try {
      const res = await api.post("/products", {
        name: newProductName,
        sku: newProductSku,
        price: parseFloat(newProductPrice),
        quantity: parseInt(newProductQuantity, 10),
        categoryId: parseInt(newProductCategoryId, 10),
      });
      setProducts([...products, res.data]);
      setNewProductName("");
      setNewProductSku("");
      setNewProductPrice("");
      setNewProductQuantity("");
      setNewProductCategoryId("");
    } catch (err) {
      setAddProductError(getErrorMessage(err, "Failed to add product."));
    } finally {
      setAddProductLoading(false);
    }
  };

  const startEditProduct = (product) => {
    setEditingProductId(product.id);
    setEditProductName(product.name);
    setEditProductSku(product.sku);
    setEditProductPrice(String(product.price));
    setEditProductQuantity(String(product.quantity));
    setEditProductCategoryId(String(product.categoryId));
    setEditProductError("");
  };

  const cancelEditProduct = () => {
    setEditingProductId(null);
    setEditProductError("");
  };

  const handleEditProductSave = async (id) => {
    setEditProductError("");
    setEditProductLoading(true);

    try {
      const res = await api.put(`/products/${id}`, {
        name: editProductName,
        sku: editProductSku,
        price: parseFloat(editProductPrice),
        quantity: parseInt(editProductQuantity, 10),
        categoryId: parseInt(editProductCategoryId, 10),
      });
      setProducts(products.map((p) => (p.id === id ? res.data : p)));
      setEditingProductId(null);
    } catch (err) {
      setEditProductError(getErrorMessage(err, "Failed to update product."));
    } finally {
      setEditProductLoading(false);
    }
  };

  const handleDeleteProduct = async (id) => {
    const confirmed = window.confirm("Are you sure you want to delete this product?");
    if (!confirmed) return;

    setProductListError("");
    setDeletingProductId(id);
    try {
      await api.delete(`/products/${id}`);
      setProducts(products.filter((p) => p.id !== id));
    } catch (err) {
      setProductListError(getErrorMessage(err, "Failed to delete product."));
    } finally {
      setDeletingProductId(null);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setAddError("");
    setAddLoading(true);

    try {
      const res = await api.post("/categories", { name: newName });
      setCategories([...categories, res.data]);
      setNewName("");
    } catch (err) {
      setAddError(getErrorMessage(err, "Failed to add category."));
    } finally {
      setAddLoading(false);
    }
  };

  const startEdit = (category) => {
    setEditingId(category.id);
    setEditName(category.name);
    setEditError("");
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditName("");
    setEditError("");
  };

  const handleEditSave = async (id) => {
    setEditError("");
    setEditLoading(true);

    try {
      const res = await api.put(`/categories/${id}`, { name: editName });
      setCategories(categories.map((c) => (c.id === id ? res.data : c)));
      setEditingId(null);
      setEditName("");
    } catch (err) {
      setEditError(getErrorMessage(err, "Failed to update category."));
    } finally {
      setEditLoading(false);
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm("Are you sure you want to delete this category?");
    if (!confirmed) return;

    setListError("");
    setDeletingId(id);
    try {
      await api.delete(`/categories/${id}`);
      setCategories(categories.filter((c) => c.id !== id));
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to delete category."));
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div style={{ maxWidth: "500px", margin: "60px auto", fontFamily: "sans-serif" }}>
      <h2>Vendor Dashboard</h2>

      <h3>Categories</h3>

      <form onSubmit={handleAdd} style={{ marginBottom: "16px" }}>
        <div style={{ display: "flex", gap: "8px" }}>
          <input
            type="text"
            name="newCategory"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="New category name"
            required
            style={{ flex: 1, padding: "8px" }}
          />
          <button type="submit" disabled={addLoading} style={{ padding: "8px 16px" }}>
            {addLoading ? "Adding..." : "Add"}
          </button>
        </div>
        {addError && <p style={{ color: "red" }}>{addError}</p>}
      </form>

      {listError && <p style={{ color: "red" }}>{listError}</p>}
      {editError && <p style={{ color: "red" }}>{editError}</p>}

      {loadingList ? (
        <p>Loading categories...</p>
      ) : categories.length === 0 ? (
        <p>No categories yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {categories.map((category) => (
            <li
              key={category.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              {editingId === category.id ? (
                <>
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    style={{ flex: 1, padding: "6px" }}
                  />
                  <button
                    onClick={() => handleEditSave(category.id)}
                    disabled={editLoading}
                    style={{ padding: "6px 12px" }}
                  >
                    {editLoading ? "Saving..." : "Save"}
                  </button>
                  <button onClick={cancelEdit} disabled={editLoading} style={{ padding: "6px 12px" }}>
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <span style={{ flex: 1 }}>{category.name}</span>
                  <button onClick={() => startEdit(category)} style={{ padding: "6px 12px" }}>
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(category.id)}
                    disabled={deletingId === category.id}
                    style={{ padding: "6px 12px" }}
                  >
                    {deletingId === category.id ? "Deleting..." : "Delete"}
                  </button>
                </>
              )}
            </li>
          ))}
        </ul>
      )}

      <h3 style={{ marginTop: "32px" }}>Products</h3>

      <form onSubmit={handleAddProduct} style={{ marginBottom: "16px" }}>
        <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
          <input
            type="text"
            value={newProductName}
            onChange={(e) => setNewProductName(e.target.value)}
            placeholder="Product name"
            required
            style={{ padding: "8px" }}
          />
          <input
            type="text"
            value={newProductSku}
            onChange={(e) => setNewProductSku(e.target.value)}
            placeholder="SKU"
            required
            style={{ padding: "8px" }}
          />
          <input
            type="number"
            step="0.01"
            value={newProductPrice}
            onChange={(e) => setNewProductPrice(e.target.value)}
            placeholder="Price"
            required
            style={{ padding: "8px" }}
          />
          <input
            type="number"
            value={newProductQuantity}
            onChange={(e) => setNewProductQuantity(e.target.value)}
            placeholder="Quantity"
            required
            style={{ padding: "8px" }}
          />
          <select
            value={newProductCategoryId}
            onChange={(e) => setNewProductCategoryId(e.target.value)}
            required
            style={{ padding: "8px" }}
          >
            <option value="" disabled>
              Select category
            </option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
          <button type="submit" disabled={addProductLoading} style={{ padding: "8px 16px" }}>
            {addProductLoading ? "Adding..." : "Add Product"}
          </button>
        </div>
        {addProductError && <p style={{ color: "red" }}>{addProductError}</p>}
      </form>

      {productListError && <p style={{ color: "red" }}>{productListError}</p>}
      {editProductError && <p style={{ color: "red" }}>{editProductError}</p>}

      {loadingProducts ? (
        <p>Loading products...</p>
      ) : products.length === 0 ? (
        <p>No products yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {products.map((product) => (
            <li
              key={product.id}
              style={{
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                {editingProductId === product.id ? (
                  <div style={{ display: "flex", flexDirection: "column", gap: "6px", flex: 1 }}>
                    <input
                      type="text"
                      value={editProductName}
                      onChange={(e) => setEditProductName(e.target.value)}
                      placeholder="Product name"
                      style={{ padding: "6px" }}
                    />
                    <input
                      type="text"
                      value={editProductSku}
                      onChange={(e) => setEditProductSku(e.target.value)}
                      placeholder="SKU"
                      style={{ padding: "6px" }}
                    />
                    <input
                      type="number"
                      step="0.01"
                      value={editProductPrice}
                      onChange={(e) => setEditProductPrice(e.target.value)}
                      placeholder="Price"
                      style={{ padding: "6px" }}
                    />
                    <input
                      type="number"
                      value={editProductQuantity}
                      onChange={(e) => setEditProductQuantity(e.target.value)}
                      placeholder="Quantity"
                      style={{ padding: "6px" }}
                    />
                    <select
                      value={editProductCategoryId}
                      onChange={(e) => setEditProductCategoryId(e.target.value)}
                      style={{ padding: "6px" }}
                    >
                      {categories.map((category) => (
                        <option key={category.id} value={category.id}>
                          {category.name}
                        </option>
                      ))}
                    </select>
                    <div style={{ display: "flex", gap: "8px" }}>
                      <button
                        onClick={() => handleEditProductSave(product.id)}
                        disabled={editProductLoading}
                        style={{ padding: "6px 12px" }}
                      >
                        {editProductLoading ? "Saving..." : "Save"}
                      </button>
                      <button
                        onClick={cancelEditProduct}
                        disabled={editProductLoading}
                        style={{ padding: "6px 12px" }}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <span style={{ flex: 1 }}>
                      {product.name} ({product.sku}) — ${product.price} — qty {product.quantity} —{" "}
                      {product.categoryName}
                    </span>
                    <button onClick={() => startEditProduct(product)} style={{ padding: "6px 12px" }}>
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteProduct(product.id)}
                      disabled={deletingProductId === product.id}
                      style={{ padding: "6px 12px" }}
                    >
                      {deletingProductId === product.id ? "Deleting..." : "Delete"}
                    </button>
                  </>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default VendorDashboard;
