import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { categoryApi } from "../../category/api/categoryApi";
import { platformApi } from "../../platform/api/platformApi";
import { productApi } from "../../product/api/productApi";
import { inventoryApi } from "../../inventory/api/inventoryApi";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

const DashboardContext = createContext(null);

export function DashboardProvider({ children }) {
  const [categories, setCategories] = useState([]);
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [categoriesError, setCategoriesError] = useState("");

  const [platforms, setPlatforms] = useState([]);
  const [loadingPlatforms, setLoadingPlatforms] = useState(true);
  const [platformsError, setPlatformsError] = useState("");

  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [productsError, setProductsError] = useState("");

  // Eager, all-products allocation data — powers the low-stock banner, which
  // needs every product's allocations up front, not just the one currently
  // expanded in InventoryPanel. useInventory.js remains the owner of
  // allocation *mutations* (allocate/update/delete); after each of those
  // succeeds it patches this copy via upsertAllocation/removeAllocation so
  // the banner doesn't go stale.
  const [allocationsByProduct, setAllocationsByProduct] = useState({});
  const [loadingAllocations, setLoadingAllocations] = useState(true);
  const [allocationsError, setAllocationsError] = useState("");

  // Bumped whenever an order is successfully created, so other hooks (like
  // useInventory) can react without DashboardContext needing to know
  // anything about orders itself.
  const [orderCreatedTick, setOrderCreatedTick] = useState(0);

  const fetchCategories = useCallback(async () => {
    setCategoriesError("");
    setLoadingCategories(true);
    try {
      const res = await categoryApi.list();
      setCategories(res.data);
    } catch (err) {
      setCategoriesError(getErrorMessage(err, "Failed to load categories."));
    } finally {
      setLoadingCategories(false);
    }
  }, []);

  const fetchPlatforms = useCallback(async () => {
    setPlatformsError("");
    setLoadingPlatforms(true);
    try {
      const res = await platformApi.list();
      setPlatforms(res.data);
    } catch (err) {
      setPlatformsError(getErrorMessage(err, "Failed to load platforms."));
    } finally {
      setLoadingPlatforms(false);
    }
  }, []);

  const fetchProducts = useCallback(async () => {
    setProductsError("");
    setLoadingProducts(true);
    try {
      const res = await productApi.list();
      setProducts(res.data);
    } catch (err) {
      setProductsError(getErrorMessage(err, "Failed to load products."));
    } finally {
      setLoadingProducts(false);
    }
  }, []);

  const fetchAllAllocations = useCallback(async (productList) => {
    if (!productList || productList.length === 0) {
      setAllocationsByProduct({});
      setLoadingAllocations(false);
      return;
    }
    setAllocationsError("");
    setLoadingAllocations(true);
    try {
      const results = await Promise.all(
        productList.map((product) =>
          inventoryApi.list(product.id).then((res) => [product.id, res.data])
        )
      );
      setAllocationsByProduct(Object.fromEntries(results));
    } catch (err) {
      setAllocationsError(getErrorMessage(err, "Failed to load allocation data."));
    } finally {
      setLoadingAllocations(false);
    }
  }, []);

  const upsertAllocation = useCallback((productId, allocation) => {
    setAllocationsByProduct((prev) => {
      const existing = prev[productId] || [];
      const idx = existing.findIndex((a) => a.platformId === allocation.platformId);
      const updated =
        idx === -1
          ? [...existing, allocation]
          : existing.map((a) => (a.platformId === allocation.platformId ? allocation : a));
      return { ...prev, [productId]: updated };
    });
  }, []);

  const removeAllocation = useCallback((productId, platformId) => {
    setAllocationsByProduct((prev) => ({
      ...prev,
      [productId]: (prev[productId] || []).filter((a) => a.platformId !== platformId),
    }));
  }, []);

  const notifyOrderCreated = useCallback(() => {
    setOrderCreatedTick((t) => t + 1);
  }, []);

  useEffect(() => {
    fetchCategories();
    fetchPlatforms();
    fetchProducts();
  }, [fetchCategories, fetchPlatforms, fetchProducts]);

  // Re-fetch all allocations whenever the product list settles/changes, so
  // newly added products or edited thresholds are reflected in the banner.
  useEffect(() => {
    if (!loadingProducts) {
      fetchAllAllocations(products);
    }
  }, [products, loadingProducts, fetchAllAllocations]);

  const value = {
    categories,
    setCategories,
    loadingCategories,
    categoriesError,

    platforms,
    setPlatforms,
    loadingPlatforms,
    platformsError,

    products,
    setProducts,
    loadingProducts,
    productsError,

    allocationsByProduct,
    loadingAllocations,
    allocationsError,
    upsertAllocation,
    removeAllocation,

    orderCreatedTick,
    notifyOrderCreated,
  };

  return (
    <DashboardContext.Provider value={value}>
      {children}
    </DashboardContext.Provider>
  );
}

export function useDashboard() {
  const ctx = useContext(DashboardContext);
  if (!ctx) {
    throw new Error("useDashboard must be used within a DashboardProvider");
  }
  return ctx;
}