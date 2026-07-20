import { Navigate } from "react-router-dom";

function ProtectedRoute({ role, children }) {
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "null");

  if (!token || !user) {
    return <Navigate to="/login?reason=session_expired" replace />;
  }

  if (user.role !== role) {
    const ownDashboard = user.role === "ADMIN" ? "/admin" : "/vendor/dashboard";
    return <Navigate to={ownDashboard} replace />;
  }

  return children;
}

export default ProtectedRoute;