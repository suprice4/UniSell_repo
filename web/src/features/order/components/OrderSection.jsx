import { useOrders } from "../hooks/useOrders";
import { useDashboard } from "../../dashboard/context/DashboardContext";

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
    confirmDeleteOrderId,
    requestDeleteOrder,
    cancelDeleteOrder,
    handleDeleteOrder,
  } = useOrders();

  const { platforms, products } = useDashboard();

  const inputClass =
    "rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500";
  const smallInputClass =
    "rounded-md border border-slate-300 px-2 py-1.5 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500";
  const secondaryBtn =
    "rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-60";
  const primaryBtn =
    "rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60";
  const dangerBtn =
    "rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 disabled:opacity-60";

  return (
    <div>
      <h3 className="text-lg font-semibold text-slate-900">Orders</h3>

      <form onSubmit={handleCreateOrderSubmit} className="mt-3">
        <div className="flex flex-col gap-2">
          <select
            value={newOrderPlatformId}
            onChange={(e) => setNewOrderPlatformId(e.target.value)}
            required
            className={inputClass}
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
            className={inputClass}
          />

          <input
            type="text"
            value={newOrderCustomerAddress}
            onChange={(e) => setNewOrderCustomerAddress(e.target.value)}
            placeholder="Customer address"
            required
            className={inputClass}
          />

          {newOrderItems.map((row, index) => (
            <div key={index} className="flex gap-2">
              <select
                value={row.productId}
                onChange={(e) => updateOrderItemRow(index, "productId", e.target.value)}
                required
                className={`flex-[2] ${inputClass}`}
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
                className={`flex-1 ${inputClass}`}
              />
              {newOrderItems.length > 1 && (
                <button type="button" onClick={() => removeOrderItemRow(index)} className={secondaryBtn}>
                  Remove
                </button>
              )}
            </div>
          ))}

          <button type="button" onClick={addOrderItemRow} className={`self-start ${secondaryBtn}`}>
            + Add Product
          </button>

          <button type="submit" disabled={createOrderLoading} className={`self-start ${primaryBtn}`}>
            {createOrderLoading ? "Creating..." : "Create Order"}
          </button>
        </div>
        {createOrderError && <p className="mt-1 text-sm text-red-600">{createOrderError}</p>}
      </form>

      {orderListError && <p className="mt-2 text-sm text-red-600">{orderListError}</p>}
      {orderActionError && <p className="mt-2 text-sm text-red-600">{orderActionError}</p>}

      {loadingOrders ? (
        <p className="mt-3 text-sm text-slate-500">Loading orders...</p>
      ) : orders.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No orders yet.</p>
      ) : (
        <ul className="mt-3 divide-y divide-slate-100">
          {orders.map((order) => (
            <li key={order.id} className="py-3">
              <div
                className="flex cursor-pointer items-center gap-2"
                onClick={() => toggleExpandOrder(order.id)}
              >
                <span className="flex-1 text-sm text-slate-800">
                  Order #{order.id} — {order.platformName} — ₱{order.totalAmount}
                </span>
                <span className="text-sm text-slate-600">{order.status}</span>
                <span className="text-xs text-slate-500">{order.paymentStatus}</span>
                <span className="text-xs text-slate-500">{order.shipmentStatus}</span>
              </div>

              {expandedOrderId === order.id && (
                <div className="mt-2 pl-4">
                  {(order.customerName || order.customerAddress) && (
                    <div className="mb-3 text-sm text-slate-700">
                      {order.customerName && <div>Customer: {order.customerName}</div>}
                      {order.customerAddress && <div>Address: {order.customerAddress}</div>}
                    </div>
                  )}

                  {(order.trackingNumber || order.courierName) && (
                    <div className="mb-3 text-sm text-slate-700">
                      {order.courierName && <div>Courier: {order.courierName}</div>}
                      {order.trackingNumber && <div>Tracking #: {order.trackingNumber}</div>}
                    </div>
                  )}

                  {itemsLoading && <p className="text-sm text-slate-500">Loading items...</p>}
                  {itemsError && <p className="text-sm text-red-600">{itemsError}</p>}

                  {orderItemsByOrderId[order.id] && (
                    <ul className="mb-3 space-y-1">
                      {orderItemsByOrderId[order.id].map((item) => (
                        <li key={item.id} className="flex items-center gap-2 text-sm text-slate-700">
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

                  <div className="flex flex-wrap gap-2">
                    {NEXT_STATUS[order.status] && (
                      <button
                        onClick={() => handleAdvanceStatus(order)}
                        disabled={
                          orderActionLoadingId === order.id ||
                          (NEXT_STATUS[order.status] === "SHIPPED" &&
                            !(shipmentTrackingByOrderId[order.id] ?? order.trackingNumber ?? "").trim())
                          || (NEXT_STATUS[order.status] === "SHIPPED" &&
                            !(shipmentCourierByOrderId[order.id] ?? order.courierName ?? "").trim())
                        }
                        className={primaryBtn}
                      >
                        {orderActionLoadingId === order.id
                          ? "Updating..."
                          : `Mark as ${NEXT_STATUS[order.status]}`}
                      </button>
                    )}

                    {order.paymentStatus === "PENDING" && order.status !== "RETURNED" && (
                      <button
                        onClick={() => handleMarkPaymentReceived(order)}
                        disabled={orderActionLoadingId === order.id}
                        className={secondaryBtn}
                      >
                        Mark Payment Received
                      </button>
                    )}

                    {order.status !== "RETURNED" && (
                      <button
                        onClick={() => handleMarkUncollected(order)}
                        disabled={orderActionLoadingId === order.id}
                        className={secondaryBtn}
                      >
                        Mark Shipment Uncollected
                      </button>
                    )}

                    {order.status === "PENDING" && (
                      <button
                        onClick={() => requestDeleteOrder(order.id)}
                        disabled={orderActionLoadingId === order.id}
                        className={dangerBtn}
                      >
                        Delete Order
                      </button>
                    )}
                  </div>

                  {confirmDeleteOrderId === order.id && (
                    <div className="mt-2 flex items-center gap-2">
                      <span className="text-sm text-red-600">Delete this order permanently?</span>
                      <button
                        onClick={() => handleDeleteOrder(order.id)}
                        disabled={orderActionLoadingId === order.id}
                        className={primaryBtn}
                      >
                        {orderActionLoadingId === order.id ? "Deleting..." : "Confirm Delete"}
                      </button>
                      <button
                        onClick={cancelDeleteOrder}
                        disabled={orderActionLoadingId === order.id}
                        className={secondaryBtn}
                      >
                        Cancel
                      </button>
                    </div>
                  )}

                  {(order.status === "PROCESSING" || order.status === "SHIPPED") && (
                    <div className="mt-2 flex flex-wrap items-center gap-2">
                      <input
                        type="text"
                        placeholder="Courier name"
                        value={shipmentCourierByOrderId[order.id] ?? order.courierName ?? ""}
                        onChange={(e) => setShipmentCourier(order.id, e.target.value)}
                        className={`flex-1 ${smallInputClass}`}
                      />
                      <input
                        type="text"
                        placeholder="Tracking number"
                        value={shipmentTrackingByOrderId[order.id] ?? order.trackingNumber ?? ""}
                        onChange={(e) => setShipmentTracking(order.id, e.target.value)}
                        className={`flex-1 ${smallInputClass}`}
                      />
                      {order.status === "SHIPPED" && (
                        <button
                          onClick={() => handleUpdateShipmentDetails(order.id)}
                          disabled={orderActionLoadingId === order.id}
                          className={secondaryBtn}
                        >
                          Save Shipment Details
                        </button>
                      )}
                    </div>
                  )}

                  {order.status !== "RETURNED" && (
                    <div className="mt-2 flex items-center gap-2">
                      <input
                        type="text"
                        placeholder="Return reason"
                        value={returnReasonByOrderId[order.id] || ""}
                        onChange={(e) => setReturnReason(order.id, e.target.value)}
                        className={`flex-1 ${smallInputClass}`}
                      />
                      <button
                        onClick={() => handleProcessReturn(order.id)}
                        disabled={orderActionLoadingId === order.id}
                        className={secondaryBtn}
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
