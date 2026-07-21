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
    <header className="flex items-center justify-between border-b border-slate-200 bg-white px-6 py-3">
      <div className="font-semibold text-slate-900">{title}</div>
      <div className="flex items-center gap-3">
        {user?.email && <span className="text-sm text-slate-600">{user.email}</span>}
        <button
          onClick={handleLogout}
          className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
        >
          Log out
        </button>
      </div>
    </header>
  );
}

export default Header;