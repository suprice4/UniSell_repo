import CategorySection from "../../category/components/CategorySection";
import PlatformSection from "../../platform/components/PlatformSection";
import ProductSection from "../../product/components/ProductSection";
import OrderSection from "../../order/components/OrderSection";

function VendorDashboard() {
  return (
    <div style={{ maxWidth: "500px", margin: "60px auto", fontFamily: "sans-serif" }}>
      <h2>Vendor Dashboard</h2>
      <CategorySection />
      <PlatformSection />
      <ProductSection />
      <OrderSection />
    </div>
  );
}

export default VendorDashboard;