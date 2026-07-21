import { NavLink, Outlet } from "react-router-dom";
import Header from "../../../core/components/Header";

const navLinkClass = ({ isActive }) =>
  `block rounded-md px-4 py-2.5 text-sm font-medium transition ${
    isActive ? "bg-slate-900 text-white" : "text-slate-700 hover:bg-slate-100"
  }`;

function AdminLayout() {
  return (
    <>
      <Header title="Admin Dashboard" />
      <div className="flex min-h-[calc(100vh-57px)]">
        <nav className="w-52 space-y-1 border-r border-slate-200 bg-white p-4">
          <NavLink to="/admin/vendors" className={navLinkClass}>
            Vendors
          </NavLink>
          <NavLink to="/admin/returns" className={navLinkClass}>
            Returns
          </NavLink>
          <NavLink to="/admin/payments" className={navLinkClass}>
            Payments
          </NavLink>
          <NavLink to="/admin/reports" className={navLinkClass}>
            Reports
          </NavLink>
          <NavLink to="/admin/activity-log" className={navLinkClass}>
            Activity Log
          </NavLink>
        </nav>
        <div className="flex-1 p-10">
          <Outlet />
        </div>
      </div>
    </>
  );
}

export default AdminLayout;
