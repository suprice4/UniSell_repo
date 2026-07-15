import { useOrders } from "../hooks/useOrders";

const NEXT_STATUS = {
  PENDING: "PROCESSING",
  PROCESSING: "SHIPPED",
  SHIPPED: "DELIVERED",
};

function OrderSection() {
  const {
    orders,
    loadingOrders,
    orderListError,
    expandedOrderId,
    orderItemsByOrderId,
    itemsLoading,
    itemsError,
    orderActionLoadingId,
    orderActionError,
    returnReasonByOrderId,
    selectedReturnItemIds,
    toggleExpandOrder,
    handleAdvanceStatus,
    handleMarkPaymentReceived,
    toggleReturnItemSelection,
    setReturnReason,
    handleProcessReturn,
    handleMarkUncollected,
  } = useOrders();

  return (
    <div>
      <h3 style={{ marginTop: "32px" }}>Orders</h3>

      {orderListError && <p style={{ color: "red" }}>{orderListError}</p>}
      {orderActionError && <p style={{ color: "red" }}>{orderActionError}</p>}

      {loadingOrders ? (
        <p>Loading orders...</p>
      ) : orders.length === 0 ? (
        <p>No orders yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {orders.map((order) => (
            <li key={order.id} style={{ borderBottom: "1px solid #eee", padding: "12px 0" }}>
              <div
                style={{ display: "flex", alignItems: "center", gap: "8px", cursor: "pointer" }}
                onClick={() => toggleExpandOrder(order.id)}
              >
                <span style={{ flex: 1 }}>
                  Order #{order.id} — {order.platformName} — ₱{order.totalAmount}
                </span>
                <span>{order.status}</span>
                <span style={{ fontSize: "12px", color: "#666" }}>{order.paymentStatus}</span>
                <span style={{ fontSize: "12px", color: "#666" }}>{order.shipmentStatus}</span>
              </div>

              {expandedOrderId === order.id && (
                <div style={{ marginTop: "8px", paddingLeft: "16px" }}>
                  {itemsLoading && <p>Loading items...</p>}
                  {itemsError && <p style={{ color: "red" }}>{itemsError}</p>}

                  {orderItemsByOrderId[order.id] && (
                    <ul style={{ listStyle: "none", padding: 0, marginBottom: "12px" }}>
                      {orderItemsByOrderId[order.id].map((item) => (
                        <li key={item.id} style={{ display: "flex", gap: "8px", padding: "4px 0" }}>
                          {order.status !== "RETURNED" && (
                            <input
                              type="checkbox"
                              checked={selectedReturnItemIds.includes(item.id)}
                              onChange={() => toggleReturnItemSelection(item.id)}
                            />
                          )}
                          <span>
                            {item.productName} × {item.quantity} @ ₱{item.priceAtTimeOfOrder}
                          </span>
                        </li>
                      ))}
                    </ul>
                  )}

                  <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
                    {NEXT_STATUS[order.status] && (
                      <button
                        onClick={() => handleAdvanceStatus(order)}
                        disabled={orderActionLoadingId === order.id}
                        style={{ padding: "6px 12px" }}
                      >
                        {orderActionLoadingId === order.id
                          ? "Updating..."
                          : `Mark as ${NEXT_STATUS[order.status]}`}
                      </button>
                    )}

                    {order.paymentStatus === "PENDING" && (
                      <button
                        onClick={() => handleMarkPaymentReceived(order)}
                        disabled={orderActionLoadingId === order.id}
                        style={{ padding: "6px 12px" }}
                      >
                        Mark Payment Received
                      </button>
                    )}

                    {order.status !== "RETURNED" && (
                      <button
                        onClick={() => handleMarkUncollected(order)}
                        disabled={orderActionLoadingId === order.id}
                        style={{ padding: "6px 12px" }}
                      >
                        Mark Shipment Uncollected
                      </button>
                    )}
                  </div>

                  {order.status !== "RETURNED" && (
                    <div style={{ marginTop: "8px", display: "flex", gap: "8px", alignItems: "center" }}>
                      <input
                        type="text"
                        placeholder="Return reason"
                        value={returnReasonByOrderId[order.id] || ""}
                        onChange={(e) => setReturnReason(order.id, e.target.value)}
                        style={{ flex: 1, padding: "6px" }}
                      />
                      <button
                        onClick={() => handleProcessReturn(order.id)}
                        disabled={orderActionLoadingId === order.id}
                        style={{ padding: "6px 12px" }}
                      >
                        Process Return (Selected Items)
                      </button>
                    </div>
                  )}
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default OrderSection;