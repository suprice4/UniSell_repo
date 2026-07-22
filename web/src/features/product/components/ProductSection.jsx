import { useProducts } from "../hooks/useProducts";
import { useCategories } from "../../category/hooks/useCategories";
import { usePlatforms } from "../../platform/hooks/usePlatforms";
import { useInventory } from "../../inventory/hooks/useInventory";
import InventoryPanel from "../../inventory/components/InventoryPanel";
import { isLowStock } from "../utils/lowStock";

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
    editPlatformId,
    editQuantity,
    setEditQuantity,
    editLoading,
    editError,
    startEditAllocation,
    cancelEditAllocation,
    handleUpdateAllocation,
  } = useInventory();

  const inputClass =
    "rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500";

  return (
    <div>
      <h3 className="text-lg font-semibold text-slate-900">Products</h3>

      <form onSubmit={handleAddProduct} className="mt-3">
        <div className="flex flex-col gap-2">
          <input
            type="text"
            value={newProductName}
            onChange={(e) => setNewProductName(e.target.value)}
            placeholder="Product name"
            required
            className={inputClass}
          />
          <input
            type="text"
            value={newProductSku}
            onChange={(e) => setNewProductSku(e.target.value)}
            placeholder="SKU"
            required
            className={inputClass}
          />
          <input
            type="number"
            step="0.01"
            value={newProductPrice}
            onChange={(e) => setNewProductPrice(e.target.value)}
            placeholder="Price"
            required
            className={inputClass}
          />
          <input
            type="number"
            value={newProductQuantity}
            onChange={(e) => setNewProductQuantity(e.target.value)}
            placeholder="Quantity"
            required
            className={inputClass}
          />
          <select
            value={newProductCategoryId}
            onChange={(e) => setNewProductCategoryId(e.target.value)}
            required
            className={inputClass}
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
          <button
            type="submit"
            disabled={addProductLoading}
            className="self-start rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {addProductLoading ? "Adding..." : "Add Product"}
          </button>
        </div>
        {addProductError && <p className="mt-1 text-sm text-red-600">{addProductError}</p>}
      </form>

      {productListError && <p className="mt-2 text-sm text-red-600">{productListError}</p>}
      {editProductError && <p className="mt-2 text-sm text-red-600">{editProductError}</p>}
      {inventoryError && <p className="mt-2 text-sm text-red-600">{inventoryError}</p>}

      {loadingProducts ? (
        <p className="mt-3 text-sm text-slate-500">Loading products...</p>
      ) : products.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No products yet.</p>
      ) : (
        <div className="mt-3">
          <div className="flex items-center gap-2 border-b border-slate-200 pb-2 text-xs font-medium uppercase tracking-wide text-slate-500">
            <span className="flex-1">Name</span>
            <span className="w-24">SKU</span>
            <span className="w-20">Price</span>
            <span className="w-16">Qty</span>
            <span className="flex-1">Category</span>
            <span>Actions</span>
          </div>
          <ul className="divide-y divide-slate-100">
          {products.map((product) => (
            <li key={product.id} className="py-2">
              <div className="flex items-center gap-2">
                {editingProductId === product.id ? (
                  <div className="flex flex-1 flex-col gap-1.5">
                    <input
                      type="text"
                      value={editProductName}
                      onChange={(e) => setEditProductName(e.target.value)}
                      placeholder="Product name"
                      className={inputClass}
                    />
                    <input
                      type="text"
                      value={editProductSku}
                      onChange={(e) => setEditProductSku(e.target.value)}
                      placeholder="SKU"
                      className={inputClass}
                    />
                    <input
                      type="number"
                      step="0.01"
                      value={editProductPrice}
                      onChange={(e) => setEditProductPrice(e.target.value)}
                      placeholder="Price"
                      className={inputClass}
                    />
                    <input
                      type="number"
                      value={editProductQuantity}
                      onChange={(e) => setEditProductQuantity(e.target.value)}
                      placeholder="Quantity"
                      className={inputClass}
                    />
                    <select
                      value={editProductCategoryId}
                      onChange={(e) => setEditProductCategoryId(e.target.value)}
                      className={inputClass}
                    >
                      {categories.map((category) => (
                        <option key={category.id} value={category.id}>
                          {category.name}
                        </option>
                      ))}
                    </select>
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEditProductSave(product.id)}
                        disabled={editProductLoading}
                        className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:opacity-60"
                      >
                        {editProductLoading ? "Saving..." : "Save"}
                      </button>
                      <button
                        onClick={cancelEditProduct}
                        disabled={editProductLoading}
                        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <span className="flex-1 text-sm text-slate-800">
                      {product.name}
                      {isLowStock(product) && (
                        <span className="ml-2 rounded-md bg-red-600 px-2 py-0.5 text-xs font-bold text-white">
                          LOW STOCK
                        </span>
                      )}
                    </span>
                    <span className="w-24 text-sm text-slate-600">{product.sku}</span>
                    <span className="w-20 text-sm text-slate-600">${product.price}</span>
                    <span className="w-16 text-sm text-slate-600">{product.quantity}</span>
                    <span className="flex-1 text-sm text-slate-600">{product.categoryName}</span>
                    <span className="flex gap-2">
                      <button
                        onClick={() => startEditProduct(product)}
                        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteProduct(product.id)}
                        disabled={deletingProductId === product.id}
                        className="rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 disabled:opacity-60"
                      >
                        {deletingProductId === product.id ? "Deleting..." : "Delete"}
                      </button>
                      <button
                        onClick={() => toggleExpandProduct(product.id)}
                        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                      >
                        {expandedProductId === product.id ? "Hide platforms" : "Platforms"}
                      </button>
                    </span>
                  </>
                )}
              </div>

              {expandedProductId === product.id && (
                <InventoryPanel
                  productId={product.id}
                  product={product}
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
                  editPlatformId={editPlatformId}
                  editQuantity={editQuantity}
                  setEditQuantity={setEditQuantity}
                  editLoading={editLoading}
                  editError={editError}
                  onStartEdit={startEditAllocation}
                  onCancelEdit={cancelEditAllocation}
                  onUpdateAllocation={handleUpdateAllocation}
                />
              )}
            </li>
          ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default ProductSection;
