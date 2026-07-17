import { useState, useEffect } from "react";
import { ordersApi } from "../api/ordersApi";

const getErrorMessage = (err, fallback) => {
  const data = err.response?.data;
  return typeof data === "string" ? data : data?.message || fallback;
};

const NEXT_STATUS = {
  PENDING: "PROCESSING",
  PROCESSING: "SHIPPED",
  SHIPPED: "DELIVERED",
};

const emptyItemRow = () => ({ productId: "", quantity: "" });

export function useOrders() {
  const [orders, setOrders] = useState([]);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [orderListError, setOrderListError] = useState("");

  const [expandedOrderId, setExpandedOrderId] = useState(null);
  const [orderItemsByOrderId, setOrderItemsByOrderId] = useState({});
  const [itemsLoading, setItemsLoading] = useState(false);
  const [itemsError, setItemsError] = useState("");

  const [orderActionLoadingId, setOrderActionLoadingId] = useState(null);
  const [orderActionError, setOrderActionError] = useState("");

  const [returnReasonByOrderId, setReturnReasonByOrderId] = useState({});
  const [selectedReturnItemIds, setSelectedReturnItemIds] = useState([]);

  const [shipmentTrackingByOrderId, setShipmentTrackingByOrderId] = useState({});
  const [shipmentCourierByOrderId, setShipmentCourierByOrderId] = useState({});

  const [newOrderPlatformId, setNewOrderPlatformId] = useState("");
  const [newOrderCustomerName, setNewOrderCustomerName] = useState("");
  const [newOrderCustomerAddress, setNewOrderCustomerAddress] = useState("");
  const [newOrderItems, setNewOrderItems] = useState([emptyItemRow()]);
  const [createOrderLoading, setCreateOrderLoading] = useState(false);
  const [createOrderError, setCreateOrderError] = useState("");

  const fetchOrders = async () => {
    setOrderListError("");
    setLoadingOrders(true);
    try {
      const res = await ordersApi.list();
      setOrders(res.data);
    } catch (err) {
      setOrderListError(getErrorMessage(err, "Failed to load orders."));
    } finally {
      setLoadingOrders(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const toggleExpandOrder = async (orderId) => {
    if (expandedOrderId === orderId) {
      setExpandedOrderId(null);
      return;
    }
    setExpandedOrderId(orderId);
    setSelectedReturnItemIds([]);
    if (orderItemsByOrderId[orderId]) return;

    setItemsError("");
    setItemsLoading(true);
    try {
      const res = await ordersApi.getItems(orderId);
      setOrderItemsByOrderId((prev) => ({ ...prev, [orderId]: res.data }));
    } catch (err) {
      setItemsError(getErrorMessage(err, "Failed to load order items."));
    } finally {
      setItemsLoading(false);
    }
  };

  const handleAdvanceStatus = async (order) => {
    const nextStatus = NEXT_STATUS[order.status];
    if (!nextStatus) return;

    setOrderActionError("");
    setOrderActionLoadingId(order.id);
    try {
      const res = await ordersApi.updateStatus(order.id, nextStatus);
      setOrders(orders.map((o) => (o.id === order.id ? res.data : o)));
    } catch (err) {
      setOrderActionError(getErrorMessage(err, "Failed to update order status."));
    } finally {
      setOrderActionLoadingId(null);
    }
  };

  const handleMarkPaymentReceived = async (order) => {
    setOrderActionError("");
    setOrderActionLoadingId(order.id);
    try {
      const res = await ordersApi.updatePaymentStatus(order.id, "RECEIVED");
      setOrders(orders.map((o) => (o.id === order.id ? res.data : o)));
    } catch (err) {
      setOrderActionError(getErrorMessage(err, "Failed to update payment status."));
    } finally {
      setOrderActionLoadingId(null);
    }
  };

  const toggleReturnItemSelection = (itemId) => {
    setSelectedReturnItemIds((prev) =>
      prev.includes(itemId) ? prev.filter((id) => id !== itemId) : [...prev, itemId]
    );
  };

  const setReturnReason = (orderId, reason) => {
    setReturnReasonByOrderId((prev) => ({ ...prev, [orderId]: reason }));
  };

  const handleProcessReturn = async (orderId) => {
    if (selectedReturnItemIds.length === 0) {
      setOrderActionError("Select at least one item to return.");
      return;
    }
    setOrderActionError("");
    setOrderActionLoadingId(orderId);
    try {
      const reason = returnReasonByOrderId[orderId] || "";
      const res = await ordersApi.processReturn(orderId, selectedReturnItemIds, reason);
      setOrders(orders.map((o) => (o.id === orderId ? res.data : o)));
      setSelectedReturnItemIds([]);
    } catch (err) {
      setOrderActionError(getErrorMessage(err, "Failed to process return."));
    } finally {
      setOrderActionLoadingId(null);
    }
  };

  const handleMarkUncollected = async (order) => {
    setOrderActionError("");
    setOrderActionLoadingId(order.id);
    try {
      const res = await ordersApi.markUncollected(order.id);
      setOrders(orders.map((o) => (o.id === order.id ? res.data : o)));
    } catch (err) {
      setOrderActionError(getErrorMessage(err, "Failed to mark shipment uncollected."));
    } finally {
      setOrderActionLoadingId(null);
    }
  };

  const setShipmentTracking = (orderId, value) => {
    setShipmentTrackingByOrderId((prev) => ({ ...prev, [orderId]: value }));
  };

  const setShipmentCourier = (orderId, value) => {
    setShipmentCourierByOrderId((prev) => ({ ...prev, [orderId]: value }));
  };

  const handleUpdateShipmentDetails = async (orderId) => {
    const trackingNumber = (shipmentTrackingByOrderId[orderId] || "").trim();
    const courierName = (shipmentCourierByOrderId[orderId] || "").trim();

    if (!trackingNumber) {
      setOrderActionError("Tracking number is required.");
      return;
    }
    if (!courierName) {
      setOrderActionError("Courier name is required.");
      return;
    }

    setOrderActionError("");
    setOrderActionLoadingId(orderId);
    try {
      const res = await ordersApi.updateShipmentDetails(orderId, trackingNumber, courierName);
      setOrders(orders.map((o) => (o.id === orderId ? res.data : o)));
    } catch (err) {
      setOrderActionError(getErrorMessage(err, "Failed to update shipment details."));
    } finally {
      setOrderActionLoadingId(null);
    }
  };

  const addOrderItemRow = () => {
    setNewOrderItems((prev) => [...prev, emptyItemRow()]);
  };

  const removeOrderItemRow = (index) => {
    setNewOrderItems((prev) => prev.filter((_, i) => i !== index));
  };

  const updateOrderItemRow = (index, field, value) => {
    setNewOrderItems((prev) =>
      prev.map((row, i) => (i === index ? { ...row, [field]: value } : row))
    );
  };

  const resetCreateOrderForm = () => {
    setNewOrderPlatformId("");
    setNewOrderCustomerName("");
    setNewOrderCustomerAddress("");
    setNewOrderItems([emptyItemRow()]);
  };

  const handleCreateOrderSubmit = async (e) => {
    e.preventDefault();
    setCreateOrderError("");

    if (!newOrderPlatformId) {
      setCreateOrderError("Select a platform.");
      return;
    }
    if (!newOrderCustomerName.trim()) {
      setCreateOrderError("Customer name is required.");
      return;
    }
    if (!newOrderCustomerAddress.trim()) {
      setCreateOrderError("Customer address is required.");
      return;
    }
    const items = newOrderItems
      .filter((row) => row.productId && row.quantity)
      .map((row) => ({ productId: Number(row.productId), quantity: Number(row.quantity) }));
    if (items.length === 0) {
      setCreateOrderError("Add at least one product with a quantity.");
      return;
    }

    setCreateOrderLoading(true);
    try {
      const res = await ordersApi.create({
        platformId: Number(newOrderPlatformId),
        customerName: newOrderCustomerName.trim(),
        customerAddress: newOrderCustomerAddress.trim(),
        items,
      });
      setOrders((prev) => [res.data, ...prev]);
      resetCreateOrderForm();
    } catch (err) {
      setCreateOrderError(getErrorMessage(err, "Failed to create order."));
    } finally {
      setCreateOrderLoading(false);
    }
  };

  return {
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
  };
}