import Header from "../../../core/components/Header";
import CategorySection from "../../category/components/CategorySection";
import PlatformSection from "../../platform/components/PlatformSection";
import ProductSection from "../../product/components/ProductSection";
import OrderSection from "../../order/components/OrderSection";
import { isLowStock, DEFAULT_LOW_STOCK_THRESHOLD } from "../../product/utils/lowStock";
import { DashboardProvider, useDashboard } from "../context/DashboardContext";

function VendorDashboardContent() {
  const { products } = useDashboard();
  const lowStockProducts = products.filter(isLowStock);

  return (
    <>
      <Header title="Vendor Dashboard" />
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
    </>
  );
}

function VendorDashboard() {
  return (
    <DashboardProvider>
      <VendorDashboardContent />
    </DashboardProvider>
  );
}

export default VendorDashboard;