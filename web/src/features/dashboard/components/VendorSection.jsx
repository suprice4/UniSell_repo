import { useVendors } from "../hooks/useVendors";

function VendorSection() {
  const {
    vendors,
    loadingList,
    listError,
    togglingId,
    toggleError,
    handleToggle,
  } = useVendors();

  return (
    <div>
      <h3>Vendor Accounts</h3>

      {listError && <p style={{ color: "red" }}>{listError}</p>}
      {toggleError && <p style={{ color: "red" }}>{toggleError}</p>}

      {loadingList ? (
        <p>Loading vendors...</p>
      ) : vendors.length === 0 ? (
        <p>No vendor accounts yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {vendors.map((vendor) => (
            <li
              key={vendor.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <span style={{ flex: 1 }}>{vendor.name}</span>
              <span style={{ flex: 1 }}>{vendor.email}</span>
              <span style={{ width: "90px", color: vendor.enabled ? "green" : "#c0392b" }}>
                {vendor.enabled ? "Active" : "Deactivated"}
              </span>
              <button
                onClick={() => handleToggle(vendor)}
                disabled={togglingId === vendor.id}
                style={{ padding: "6px 12px" }}
              >
                {togglingId === vendor.id
                  ? "Updating..."
                  : vendor.enabled
                  ? "Deactivate"
                  : "Activate"}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default VendorSection;