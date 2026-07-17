import { useOrders } from "../hooks/useOrders";
import { usePlatforms } from "../../platform/hooks/usePlatforms";
import { useProducts } from "../../product/hooks/useProducts";

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
    shipmentTrackingByOrderId,
    shipmentCourierByOrderId,
    setShipmentTracking,
    setShipmentCourier,
    handleUpdateShipmentDetails,
    newOrderPlatformId,
    setNewOrderPlatformId,
    newOrderCustomerName,
    setNewOrderCustomerName,
    newOrderCustomerAddress,
    setNewOrderCustomerAddress,
    newOrderItems,
    addOrderItemRow,
    removeOrderItemRow,
    updateOrderItemRow,
    createOrderLoading,
    createOrderError,
    handleCreateOrderSubmit,
  } = useOrders();

  const { platforms } = usePlatforms();
  const { products } = useProducts();

  return (
    <div>
      <h3 style={{ marginTop: "32px" }}>Orders</h3>

      <form onSubmit={handleCreateOrderSubmit} style={{ marginBottom: "16px" }}>
        <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
          <select
            value={newOrderPlatformId}
            onChange={(e) => setNewOrderPlatformId(e.target.value)}
            required
            style={{ padding: "8px" }}
          >
            <option value="" disabled>
              Select platform
            </option>
            {platforms.map((platform) => (
              <option key={platform.id} value={platform.id}>
                {platform.name}
              </option>
            ))}
          </select>

          <input
            type="text"
            value={newOrderCustomerName}
            onChange={(e) => setNewOrderCustomerName(e.target.value)}
            placeholder="Customer name"
            required
            style={{ padding: "8px" }}
          />

          <input
            type="text"
            value={newOrderCustomerAddress}
            onChange={(e) => setNewOrderCustomerAddress(e.target.value)}
            placeholder="Customer address"
            required
            style={{ padding: "8px" }}
          />

          {newOrderItems.map((row, index) => (
            <div key={index} style={{ display: "flex", gap: "8px" }}>
              <select
                value={row.productId}
                onChange={(e) => updateOrderItemRow(index, "productId", e.target.value)}
                required
                style={{ flex: 2, padding: "8px" }}
              >
                <option value="" disabled>
                  Select product
                </option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} ({product.sku})
                  </option>
                ))}
              </select>
              <input
                type="number"
                min="1"
                value={row.quantity}
                onChange={(e) => updateOrderItemRow(index, "quantity", e.target.value)}
                placeholder="Qty"
                required
                style={{ flex: 1, padding: "8px" }}
              />
              {newOrderItems.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeOrderItemRow(index)}
                  style={{ padding: "8px 12px" }}
                >
                  Remove
                </button>
              )}
            </div>
          ))}

          <button type="button" onClick={addOrderItemRow} style={{ padding: "8px 16px", alignSelf: "flex-start" }}>
            + Add Product
          </button>

          <button type="submit" disabled={createOrderLoading} style={{ padding: "8px 16px" }}>
            {createOrderLoading ? "Creating..." : "Create Order"}
          </button>
        </div>
        {createOrderError && <p style={{ color: "red" }}>{createOrderError}</p>}
      </form>

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
                  {(order.customerName || order.customerAddress) && (
                    <div style={{ marginBottom: "12px", fontSize: "14px", color: "#333" }}>
                      {order.customerName && <div>Customer: {order.customerName}</div>}
                      {order.customerAddress && <div>Address: {order.customerAddress}</div>}
                    </div>
                  )}

                  {(order.trackingNumber || order.courierName) && (
                    <div style={{ marginBottom: "12px", fontSize: "14px", color: "#333" }}>
                      {order.courierName && <div>Courier: {order.courierName}</div>}
                      {order.trackingNumber && <div>Tracking #: {order.trackingNumber}</div>}
                    </div>
                  )}

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

                  {order.status === "SHIPPED" && (
                    <div style={{ marginTop: "8px", display: "flex", gap: "8px", alignItems: "center", flexWrap: "wrap" }}>
                      <input
                        type="text"
                        placeholder="Courier name"
                        value={shipmentCourierByOrderId[order.id] ?? order.courierName ?? ""}
                        onChange={(e) => setShipmentCourier(order.id, e.target.value)}
                        style={{ flex: 1, padding: "6px" }}
                      />
                      <input
                        type="text"
                        placeholder="Tracking number"
                        value={shipmentTrackingByOrderId[order.id] ?? order.trackingNumber ?? ""}
                        onChange={(e) => setShipmentTracking(order.id, e.target.value)}
                        style={{ flex: 1, padding: "6px" }}
                      />
                      <button
                        onClick={() => handleUpdateShipmentDetails(order.id)}
                        disabled={orderActionLoadingId === order.id}
                        style={{ padding: "6px 12px" }}
                      >
                        Save Shipment Details
                      </button>
                    </div>
                  )}

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