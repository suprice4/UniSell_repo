import { usePayments } from "../hooks/usePayments";

function PaymentsSection() {
  const { payments, loadingList, listError } = usePayments();

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-slate-900">Payment Records</h3>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading payments...</p>
      ) : payments.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No orders yet.</p>
      ) : (
        <ul className="mt-3 divide-y divide-slate-100">
          {payments.map((p) => (
            <li key={p.orderId} className="flex items-center gap-2 py-2.5">
              <span className="flex-1 text-sm text-slate-800">Order #{p.orderId}</span>
              <span className="flex-1 text-sm text-slate-600">{p.vendorName}</span>
              <span className="flex-1 text-sm text-slate-600">{p.vendorEmail}</span>
              <span className="flex-1 text-sm text-slate-600">{p.platformName}</span>
              <span className="flex-1 text-sm text-slate-800">₱{p.totalAmount?.toFixed(2)}</span>
              <span
                className={`w-28 text-sm font-medium ${
                  p.paymentStatus === "PAID" ? "text-green-600" : "text-red-600"
                }`}
              >
                {p.paymentStatus}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default PaymentsSection;
