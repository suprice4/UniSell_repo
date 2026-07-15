import api from "../../../core/api/axios";

export const ordersApi = {
  list: () => api.get("/orders"),
  getItems: (orderId) => api.get(`/orders/${orderId}/items`),
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }),
  updatePaymentStatus: (id, paymentStatus) => api.put(`/orders/${id}/payment-status`, { paymentStatus }),
  processReturn: (id, orderItemIds, reason) => api.put(`/orders/${id}/return`, { orderItemIds, reason }),
  markUncollected: (id) => api.put(`/orders/${id}/shipment-status`, { status: "UNCOLLECTED" }),
};