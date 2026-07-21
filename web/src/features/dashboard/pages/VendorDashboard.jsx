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
      <div className="mx-auto max-w-2xl px-4 py-10">
        <h2 className="text-xl font-semibold text-slate-900">Vendor Dashboard</h2>

        {lowStockEntries.length > 0 && (
          <div className="mt-4 rounded-md border border-red-200 bg-red-50 p-3">
            <strong className="text-sm text-red-700">
              Low stock alert: {lowStockEntries.length} product
              {lowStockEntries.length > 1 ? "s" : ""} below threshold
            </strong>
            <ul className="mt-2 list-disc space-y-0.5 pl-5">
              {lowStockEntries.map(({ product, lowTotal, lowAllocations }) => (
                <li key={product.id} className="text-sm text-red-700">
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

        <div className="mt-6 space-y-6">
          <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <CategorySection />
          </section>
          <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <PlatformSection />
          </section>
          <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <ProductSection />
          </section>
          <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <OrderSection />
          </section>
        </div>
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
