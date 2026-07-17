import CategorySection from "../../category/components/CategorySection";
import PlatformSection from "../../platform/components/PlatformSection";
import ProductSection from "../../product/components/ProductSection";
import OrderSection from "../../order/components/OrderSection";
import { useProducts } from "../../product/hooks/useProducts";

const DEFAULT_LOW_STOCK_THRESHOLD = 5;

const isLowStock = (product) =>
  product.quantity < (product.lowStockThreshold ?? DEFAULT_LOW_STOCK_THRESHOLD);

function VendorDashboard() {
  // Separate useProducts() call from ProductSection's own — this hook is
  // self-contained (its own fetch/state), so this is an extra GET /products
  // on dashboard load rather than shared state. Acceptable for today;
  // lifting this into a shared parent is a post-deployment cleanup.
  const { products } = useProducts();
  const lowStockProducts = products.filter(isLowStock);

  return (
    <div style={{ maxWidth: "500px", margin: "60px auto", fontFamily: "sans-serif" }}>
      <h2>Vendor Dashboard</h2>

      {lowStockProducts.length > 0 && (
        <div
          style={{
            backgroundColor: "#fdecea",
            border: "1px solid #c0392b",
            borderRadius: "4px",
            padding: "12px",
            marginBottom: "16px",
          }}
        >
          <strong style={{ color: "#c0392b" }}>
            Low stock alert: {lowStockProducts.length} product
            {lowStockProducts.length > 1 ? "s" : ""} below threshold
          </strong>
          <ul style={{ margin: "8px 0 0", paddingLeft: "20px" }}>
            {lowStockProducts.map((product) => (
              <li key={product.id} style={{ fontSize: "14px" }}>
                {product.name} ({product.sku}) — qty {product.quantity}
                {" "}(threshold {product.lowStockThreshold ?? DEFAULT_LOW_STOCK_THRESHOLD})
              </li>
            ))}
          </ul>
        </div>
      )}

      <CategorySection />
      <PlatformSection />
      <ProductSection />
      <OrderSection />
    </div>
  );
}

export default VendorDashboard;