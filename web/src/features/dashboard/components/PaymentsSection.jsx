import { usePayments } from "../hooks/usePayments";

function PaymentsSection() {
  const { payments, loadingList, listError } = usePayments();

  return (
    <div>
      <h3>Payment Records</h3>

      {listError && <p style={{ color: "red" }}>{listError}</p>}

      {loadingList ? (
        <p>Loading payments...</p>
      ) : payments.length === 0 ? (
        <p>No orders yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {payments.map((p) => (
            <li
              key={p.orderId}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <span style={{ flex: 1 }}>Order #{p.orderId}</span>
              <span style={{ flex: 1 }}>{p.vendorName}</span>
              <span style={{ flex: 1 }}>{p.vendorEmail}</span>
              <span style={{ flex: 1 }}>{p.platformName}</span>
              <span style={{ flex: 1 }}>
                ₱{p.totalAmount?.toFixed(2)}
              </span>
              <span
                style={{
                  width: "110px",
                  color: p.paymentStatus === "PAID" ? "green" : "#c0392b",
                }}
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