import { Routes, Route, Navigate } from 'react-router-dom'
import Register from './features/auth/pages/Register'
import Login from './features/auth/pages/Login'
import VendorDashboard from './features/dashboard/pages/VendorDashboard'
import AdminLayout from './features/dashboard/layout/AdminLayout'
import AdminVendorsPage from './features/dashboard/pages/AdminVendorsPage'
import AdminReturnsPage from './features/dashboard/pages/AdminReturnsPage'
import AdminPaymentsPage from './features/dashboard/pages/AdminPaymentsPage'
import AdminReportsPage from './features/dashboard/pages/AdminReportsPage'
import ProtectedRoute from './core/routing/ProtectedRoute'
import ActivityLogPage from './features/admin/activityLog/pages/ActivityLogPage'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route
        path="/vendor/dashboard"
        element={
          <ProtectedRoute role="VENDOR">
            <VendorDashboard />
          </ProtectedRoute>
        }
      />

      {/* Admin section: shared sidebar layout, each section is its own route */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute role="ADMIN">
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="vendors" replace />} />
        <Route path="vendors" element={<AdminVendorsPage />} />
        <Route path="returns" element={<AdminReturnsPage />} />
        <Route path="payments" element={<AdminPaymentsPage />} />
        <Route path="reports" element={<AdminReportsPage />} />
        <Route path="activity-log" element={<ActivityLogPage />} />
      </Route>

      {/* Old single-page URL — redirect so nothing bookmarked breaks */}
      <Route path="/admin/dashboard" element={<Navigate to="/admin/vendors" replace />} />
    </Routes>
  )
}

export default App