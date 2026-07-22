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
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-slate-900">Vendor Accounts</h3>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}
      {toggleError && <p className="mt-2 text-sm text-red-600">{toggleError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading vendors...</p>
      ) : vendors.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No vendor accounts yet.</p>
      ) : (
        <div className="mt-3">
          <div className="flex items-center gap-2 border-b border-slate-200 pb-2 text-xs font-medium uppercase tracking-wide text-slate-500">
            <span className="flex-1">Name</span>
            <span className="flex-1">Email</span>
            <span className="w-24">Status</span>
            <span className="w-28 text-right">Action</span>
          </div>
          <ul className="divide-y divide-slate-100">
            {vendors.map((vendor) => (
              <li key={vendor.id} className="flex items-center gap-2 py-2.5">
                <span className="flex-1 text-sm text-slate-800">{vendor.name}</span>
                <span className="flex-1 text-sm text-slate-600">{vendor.email}</span>
                <span
                  className={`w-24 text-sm font-medium ${
                    vendor.enabled ? "text-green-600" : "text-red-600"
                  }`}
                >
                  {vendor.enabled ? "Active" : "Deactivated"}
                </span>
                <span className="w-28 text-right">
                  <button
                    onClick={() => handleToggle(vendor)}
                    disabled={togglingId === vendor.id}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
                  >
                    {togglingId === vendor.id
                      ? "Updating..."
                      : vendor.enabled
                      ? "Deactivate"
                      : "Activate"}
                  </button>
                </span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default VendorSection;
