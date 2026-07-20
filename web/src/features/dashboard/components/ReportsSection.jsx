import { useReports } from "../hooks/useReports";

function ReportsSection() {
  const { reports, loadingList, listError } = useReports();

  return (
    <div>
      <h3>Vendor Reports</h3>

      {listError && <p style={{ color: "red" }}>{listError}</p>}

      {loadingList ? (
        <p>Loading reports...</p>
      ) : reports.length === 0 ? (
        <p>No vendors yet.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ borderBottom: "2px solid #ccc", textAlign: "left" }}>
              <th style={{ padding: "8px" }}>Vendor</th>
              <th style={{ padding: "8px" }}>Email</th>
              <th style={{ padding: "8px" }}>Total Orders</th>
              <th style={{ padding: "8px" }}>Inventory</th>
              <th style={{ padding: "8px" }}>Pending</th>
              <th style={{ padding: "8px" }}>Received</th>
              <th style={{ padding: "8px" }}>Refunded</th>
            </tr>
          </thead>
          <tbody>
            {reports.map((r) => (
              <tr key={r.vendorId} style={{ borderBottom: "1px solid #eee" }}>
                <td style={{ padding: "8px" }}>{r.vendorName}</td>
                <td style={{ padding: "8px" }}>{r.vendorEmail}</td>
                <td style={{ padding: "8px" }}>{r.totalOrders}</td>
                <td style={{ padding: "8px" }}>{r.totalInventory}</td>
                <td style={{ padding: "8px" }}>{r.paymentStatusBreakdown.PENDING ?? 0}</td>
                <td style={{ padding: "8px" }}>{r.paymentStatusBreakdown.RECEIVED ?? 0}</td>
                <td style={{ padding: "8px" }}>{r.paymentStatusBreakdown.REFUNDED ?? 0}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ReportsSection;