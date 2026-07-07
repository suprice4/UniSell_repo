import api from "../api/axios"; // adjust relative path if VendorDashboard isn't in the same folder depth as Login.jsx

function VendorDashboard() {
  const testAuth = async () => {
    const before = localStorage.getItem("token");
    const res = await api.get("/test/protected");
    const after = localStorage.getItem("token");
    console.log("Response:", res.data);
    console.log("Token changed:", before !== after);
    console.log("Before (last 15):", before?.slice(-15));
    console.log("After  (last 15):", after?.slice(-15));
  };

  return (
    <div>
      <h2>Vendor Dashboard</h2>
      <button onClick={testAuth}>Test Auth (temp)</button>
    </div>
  );
}

export default VendorDashboard;