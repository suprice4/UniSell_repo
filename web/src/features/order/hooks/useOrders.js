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
  };
}