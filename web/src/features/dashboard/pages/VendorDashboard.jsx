import Header from "../../../core/components/Header";
import CategorySection from "../../category/components/CategorySection";
import PlatformSection from "../../platform/components/PlatformSection";
import ProductSection from "../../product/components/ProductSection";
import OrderSection from "../../order/components/OrderSection";
import {
  isLowStock,
  isLowStockAllocation,
  DEFAULT_LOW_STOCK_THRESHOLD,
} from "../../product/utils/lowStock";
import { DashboardProvider, useDashboard } from "../context/DashboardContext";

function VendorDashboardContent() {
  const { products, allocationsByProduct } = useDashboard();

  const lowStockEntries = products
    .map((product) => {
      const allocations = allocationsByProduct[product.id] || [];
      const lowAllocations = allocations.filter((alloc) => isLowStockAllocation(alloc, product));
      const lowTotal = isLowStock(product);
      if (!lowTotal && lowAllocations.length === 0) return null;
      return { product, lowTotal, lowAllocations };
    })
    .filter(Boolean);

  return (
    <>
      <Header title="Vendor Dashboard" />
      <div style={{ maxWidth: "500px", margin: "60px auto", fontFamily: "sans-serif" }}>
        <h2>Vendor Dashboard</h2>

        {lowStockEntries.length > 0 && (
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
              Low stock alert: {lowStockEntries.length} product
              {lowStockEntries.length > 1 ? "s" : ""} below threshold
            </strong>
            <ul style={{ margin: "8px 0 0", paddingLeft: "20px" }}>
              {lowStockEntries.map(({ product, lowTotal, lowAllocations }) => (
                <li key={product.id} style={{ fontSize: "14px" }}>
                  {product.name} ({product.sku})
                  {lowTotal && (
                    <>
                      {" "}— total qty {product.quantity} (threshold{" "}
                      {product.lowStockThreshold ?? DEFAULT_LOW_STOCK_THRESHOLD})
                    </>
                  )}
                  {lowAllocations.length > 0 && (
                    <>
                      {lowTotal ? "; " : " — "}
                      low on{" "}
                      {lowAllocations
                        .map((a) => `${a.platformName} (${a.allocatedQuantity})`)
                        .join(", ")}
                    </>
                  )}
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