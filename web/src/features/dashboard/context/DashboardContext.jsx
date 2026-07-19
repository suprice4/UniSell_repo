import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { categoryApi } from "../../category/api/categoryApi";
import { platformApi } from "../../platform/api/platformApi";
import { productApi } from "../../product/api/productApi";
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

  useEffect(() => {
    fetchCategories();
    fetchPlatforms();
    fetchProducts();
  }, [fetchCategories, fetchPlatforms, fetchProducts]);

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