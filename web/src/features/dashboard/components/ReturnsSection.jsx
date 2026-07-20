import { useReturns } from "../hooks/useReturns";

function ReturnsSection() {
  const { returns, loadingList, listError } = useReturns();

  return (
    <div>
      <h3>Return Records</h3>

      {listError && <p style={{ color: "red" }}>{listError}</p>}

      {loadingList ? (
        <p>Loading returns...</p>
      ) : returns.length === 0 ? (
        <p>No return records yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {returns.map((r) => (
            <li
              key={r.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <span style={{ flex: 1 }}>Order #{r.orderId}</span>
              <span style={{ flex: 1 }}>{r.vendorName}</span>
              <span style={{ flex: 1 }}>{r.vendorEmail}</span>
              <span style={{ flex: 1 }}>
                {r.productName} x{r.quantity}
              </span>
              <span style={{ flex: 1 }}>{r.reason || "—"}</span>
              <span style={{ flex: 1 }}>
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