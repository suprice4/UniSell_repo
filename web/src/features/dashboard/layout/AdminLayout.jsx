import { NavLink, Outlet } from "react-router-dom";
import Header from "../../../core/components/Header";

const linkStyle = ({ isActive }) => ({
  display: "block",
  padding: "10px 16px",
  borderRadius: "6px",
  textDecoration: "none",
  color: isActive ? "#fff" : "#333",
  backgroundColor: isActive ? "#2c3e50" : "transparent",
  marginBottom: "4px",
  fontWeight: isActive ? 600 : 400,
});

function AdminLayout() {
  return (
    <>
      <Header title="Admin Dashboard" />
      <div style={{ display: "flex", minHeight: "calc(100vh - 60px)" }}>
        <nav
          style={{
            width: "200px",
            padding: "24px 12px",
            borderRight: "1px solid #eee",
          }}
        >
          <NavLink to="/admin/vendors" style={linkStyle}>
            Vendors
          </NavLink>
          <NavLink to="/admin/returns" style={linkStyle}>
            Returns
          </NavLink>
          <NavLink to="/admin/payments" style={linkStyle}>
            Payments
          </NavLink>
          <NavLink to="/admin/reports" style={linkStyle}>
            Reports
          </NavLink>
        </nav>
        <div style={{ flex: 1, padding: "40px", fontFamily: "sans-serif" }}>
          <Outlet />
        </div>
      </div>
    </>
  );
}

export default AdminLayout;