import { useNavigate } from "react-router-dom";

function Header({ title }) {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <header
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "12px 24px",
        borderBottom: "1px solid #ddd",
        fontFamily: "sans-serif",
      }}
    >
      <div style={{ fontWeight: "bold" }}>{title}</div>
      <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
        {user?.email && <span style={{ fontSize: "14px" }}>{user.email}</span>}
        <button onClick={handleLogout} style={{ padding: "6px 12px" }}>
          Log out
        </button>
      </div>
    </header>
  );
}

export default Header;