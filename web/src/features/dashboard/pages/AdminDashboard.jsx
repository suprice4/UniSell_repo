import Header from "../../../core/components/Header";
import VendorSection from "../components/VendorSection";

function AdminDashboard() {
  return (
    <>
      <Header title="Admin Dashboard" />
      <div style={{ padding: "40px", fontFamily: "sans-serif" }}>
        <VendorSection />
      </div>
    </>
  );
}

export default AdminDashboard;