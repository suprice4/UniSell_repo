import api from "../../../core/api/axios";
export const ordersApi = {
  list: () => api.get("/orders"),
  getItems: (orderId) => api.get(`/orders/${orderId}/items`),
  create: (orderData) => api.post("/orders", orderData),
  updateStatus: (id, status, trackingNumber, courierName) =>
  api.put(`/orders/${id}/status`, { status, trackingNumber, courierName }),
  updatePaymentStatus: (id, paymentStatus) => api.put(`/orders/${id}/payment-status`, { paymentStatus }),
  processReturn: (id, orderItemIds, reason) => api.put(`/orders/${id}/return`, { orderItemIds, reason }),
  markUncollected: (id) => api.put(`/orders/${id}/shipment-status`, { status: "UNCOLLECTED" }),
  updateShipmentDetails: (id, trackingNumber, courierName) =>
  api.put(`/orders/${id}/shipment-details`, { trackingNumber, courierName }),
  remove: (id) => api.delete(`/orders/${id}`),
};