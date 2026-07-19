import { useState } from "react";
import { productApi } from "../api/productApi";
import { getErrorMessage } from "../../../core/api/getErrorMessage";
import { useDashboard } from "../../dashboard/context/DashboardContext";

export function useProducts() {
  const { products, setProducts, loadingProducts, productsError } = useDashboard();

  const [newProductName, setNewProductName] = useState("");
  const [newProductSku, setNewProductSku] = useState("");
  const [newProductPrice, setNewProductPrice] = useState("");
  const [newProductQuantity, setNewProductQuantity] = useState("");
  const [newProductCategoryId, setNewProductCategoryId] = useState("");
  const [addProductLoading, setAddProductLoading] = useState(false);
  const [addProductError, setAddProductError] = useState("");

  const [deletingProductId, setDeletingProductId] = useState(null);
  const [deleteProductListError, setDeleteProductListError] = useState("");

  const [editingProductId, setEditingProductId] = useState(null);
  const [editProductName, setEditProductName] = useState("");
  const [editProductSku, setEditProductSku] = useState("");
  const [editProductPrice, setEditProductPrice] = useState("");
  const [editProductQuantity, setEditProductQuantity] = useState("");
  const [editProductCategoryId, setEditProductCategoryId] = useState("");
  const [editProductLoading, setEditProductLoading] = useState(false);
  const [editProductError, setEditProductError] = useState("");

  const handleAddProduct = async (e) => {
    e.preventDefault();
    setAddProductError("");
    setAddProductLoading(true);
    try {
      const res = await productApi.create({
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
      const res = await productApi.update(id, {
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
    setDeleteProductListError("");
    setDeletingProductId(id);
    try {
      await productApi.remove(id);
      setProducts(products.filter((p) => p.id !== id));
    } catch (err) {
      setDeleteProductListError(getErrorMessage(err, "Failed to delete product."));
    } finally {
      setDeletingProductId(null);
    }
  };

  return {
    products,
    loadingProducts,
    productListError: productsError || deleteProductListError,
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
  };
}