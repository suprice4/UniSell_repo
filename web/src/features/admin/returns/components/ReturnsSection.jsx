import { useReturns } from "../hooks/useReturns";

function ReturnsSection() {
  const { returns, loadingList, listError } = useReturns();

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-slate-900">Return Records</h3>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading returns...</p>
      ) : returns.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No return records yet.</p>
      ) : (
        <ul className="mt-3 divide-y divide-slate-100">
          {returns.map((r) => (
            <li key={r.id} className="flex items-center gap-2 py-2.5">
              <span className="flex-1 text-sm text-slate-800">Order #{r.orderId}</span>
              <span className="flex-1 text-sm text-slate-600">{r.vendorName}</span>
              <span className="flex-1 text-sm text-slate-600">{r.vendorEmail}</span>
              <span className="flex-1 text-sm text-slate-600">
                {r.productName} x{r.quantity}
              </span>
              <span className="flex-1 text-sm text-slate-600">{r.reason || "—"}</span>
              <span className="flex-1 text-sm text-slate-500">
                {new Date(r.returnedAt).toLocaleString()}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ReturnsSection;
