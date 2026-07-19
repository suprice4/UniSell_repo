import { useState } from "react";
import { inventoryApi } from "../api/inventoryApi";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

export function useInventory() {
  const [expandedProductId, setExpandedProductId] = useState(null);
  const [inventoryByProduct, setInventoryByProduct] = useState({});
  const [loadingInventoryFor, setLoadingInventoryFor] = useState(null);
  const [inventoryError, setInventoryError] = useState("");

  const [allocPlatformId, setAllocPlatformId] = useState("");
  const [allocQuantity, setAllocQuantity] = useState("");
  const [allocLoading, setAllocLoading] = useState(false);
  const [allocError, setAllocError] = useState("");

  const [editPlatformId, setEditPlatformId] = useState(null);
  const [editQuantity, setEditQuantity] = useState("");
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState("");

  const fetchInventoryForProduct = async (productId) => {
    setInventoryError("");
    setLoadingInventoryFor(productId);
    try {
      const res = await inventoryApi.list(productId);
      setInventoryByProduct((prev) => ({ ...prev, [productId]: res.data }));
    } catch (err) {
      setInventoryError(getErrorMessage(err, "Failed to load allocations."));
    } finally {
      setLoadingInventoryFor(null);
    }
  };

  const toggleExpandProduct = (productId) => {
    if (expandedProductId === productId) {
      setExpandedProductId(null);
      return;
    }
    setExpandedProductId(productId);
    setAllocPlatformId("");
    setAllocQuantity("");
    setAllocError("");
    setEditPlatformId(null);
    setEditQuantity("");
    setEditError("");
    fetchInventoryForProduct(productId);
  };

  const handleAllocate = async (e, productId) => {
    e.preventDefault();
    setAllocError("");
    setAllocLoading(true);
    try {
      const res = await inventoryApi.allocate(
        productId,
        parseInt(allocPlatformId, 10),
        parseInt(allocQuantity, 10)
      );
      setInventoryByProduct((prev) => ({
        ...prev,
        [productId]: [...(prev[productId] || []), res.data],
      }));
      setAllocPlatformId("");
      setAllocQuantity("");
    } catch (err) {
      setAllocError(getErrorMessage(err, "Failed to allocate stock."));
    } finally {
      setAllocLoading(false);
    }
  };

  const handleDeleteAllocation = async (productId, platformId) => {
    const confirmed = window.confirm("Remove this platform allocation?");
    if (!confirmed) return;
    setInventoryError("");
    try {
      await inventoryApi.remove(productId, platformId);
      setInventoryByProduct((prev) => ({
        ...prev,
        [productId]: (prev[productId] || []).filter((a) => a.platformId !== platformId),
      }));
    } catch (err) {
      setInventoryError(getErrorMessage(err, "Failed to remove allocation."));
    }
  };

  const startEditAllocation = (platformId, currentQuantity) => {
    setEditPlatformId(platformId);
    setEditQuantity(String(currentQuantity));
    setEditError("");
  };

  const cancelEditAllocation = () => {
    setEditPlatformId(null);
    setEditQuantity("");
    setEditError("");
  };

  const handleUpdateAllocation = async (productId, platformId, quantity) => {
    setEditError("");
    setEditLoading(true);
    try {
      const res = await inventoryApi.update(productId, platformId, parseInt(quantity, 10));
      setInventoryByProduct((prev) => ({
        ...prev,
        [productId]: (prev[productId] || []).map((a) =>
          a.platformId === platformId ? res.data : a
        ),
      }));
      setEditPlatformId(null);
      setEditQuantity("");
    } catch (err) {
      setEditError(getErrorMessage(err, "Failed to update allocation."));
    } finally {
      setEditLoading(false);
    }
  };

  return {
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
  };
}