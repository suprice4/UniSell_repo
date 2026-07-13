import { useProducts } from "../hooks/useProducts";
import { useCategories } from "../../category/hooks/useCategories";
import { usePlatforms } from "../../platform/hooks/usePlatforms";
import { useInventory } from "../../inventory/hooks/useInventory";
import InventoryPanel from "../../inventory/components/InventoryPanel";

function ProductSection() {
  const {
    products,
    loadingProducts,
    productListError,
    newProductName,
    setNewProductName,
    newProductSku,
    setNewProductSku,
    newProductPrice,
    setNewProductPrice,
    newProductQuantity,
    setNewProductQuantity,
    newProductCategoryId,
    setNewProductCategoryId,
    addProductLoading,
    addProductError,
    deletingProductId,
    editingProductId,
    editProductName,
    setEditProductName,
    editProductSku,
    setEditProductSku,
    editProductPrice,
    setEditProductPrice,
    editProductQuantity,
    setEditProductQuantity,
    editProductCategoryId,
    setEditProductCategoryId,
    editProductLoading,
    editProductError,
    handleAddProduct,
    startEditProduct,
    cancelEditProduct,
    handleEditProductSave,
    handleDeleteProduct,
  } = useProducts();

  const { categories } = useCategories();
  const { platforms } = usePlatforms();

  const {
    expandedProductId,
    inventoryByProduct,
    loadingInventoryFor,
    inventoryError,
    allocPlatformId,
    setAllocPlatformId,
    allocQuantity,
    setAllocQuantity,
    allocLoading,
    allocError,
    toggleExpandProduct,
    handleAllocate,
    handleDeleteAllocation,
  } = useInventory();

  return (
    <div>
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
      {inventoryError && <p style={{ color: "red" }}>{inventoryError}</p>}

      {loadingProducts ? (
        <p>Loading products...</p>
      ) : products.length === 0 ? (
        <p>No products yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {products.map((product) => (
            <li key={product.id} style={{ padding: "8px 0", borderBottom: "1px solid #eee" }}>
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
                    <button onClick={() => toggleExpandProduct(product.id)} style={{ padding: "6px 12px" }}>
                      {expandedProductId === product.id ? "Hide platforms" : "Platforms"}
                    </button>
                  </>
                )}
              </div>

              {expandedProductId === product.id && (
                <InventoryPanel
                  productId={product.id}
                  platforms={platforms}
                  allocations={inventoryByProduct[product.id]}
                  loading={loadingInventoryFor === product.id}
                  allocPlatformId={allocPlatformId}
                  setAllocPlatformId={setAllocPlatformId}
                  allocQuantity={allocQuantity}
                  setAllocQuantity={setAllocQuantity}
                  allocLoading={allocLoading}
                  allocError={allocError}
                  onAllocate={handleAllocate}
                  onDeleteAllocation={handleDeleteAllocation}
                />
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ProductSection;