import { useReports } from "../hooks/useReports";

function ReportsSection() {
  const { reports, loadingList, listError } = useReports();

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-slate-900">Vendor Reports</h3>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading reports...</p>
      ) : reports.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No vendors yet.</p>
      ) : (
        <div className="mt-3 overflow-x-auto">
          <table className="w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-slate-500">
                <th className="px-3 py-2 font-medium">Vendor</th>
                <th className="px-3 py-2 font-medium">Email</th>
                <th className="px-3 py-2 font-medium">Total Orders</th>
                <th className="px-3 py-2 font-medium">Inventory</th>
                <th className="px-3 py-2 font-medium">Pending</th>
                <th className="px-3 py-2 font-medium">Received</th>
                <th className="px-3 py-2 font-medium">Refunded</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {reports.map((r) => (
                <tr key={r.vendorId} className="text-slate-700">
                  <td className="px-3 py-2">{r.vendorName}</td>
                  <td className="px-3 py-2">{r.vendorEmail}</td>
                  <td className="px-3 py-2">{r.totalOrders}</td>
                  <td className="px-3 py-2">{r.totalInventory}</td>
                  <td className="px-3 py-2">{r.paymentStatusBreakdown.PENDING ?? 0}</td>
                  <td className="px-3 py-2">{r.paymentStatusBreakdown.RECEIVED ?? 0}</td>
                  <td className="px-3 py-2">{r.paymentStatusBreakdown.REFUNDED ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default ReportsSection;
