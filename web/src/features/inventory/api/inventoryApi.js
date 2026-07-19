import api from "../../../core/api/axios";

export const inventoryApi = {
  list: (productId) => api.get(`/products/${productId}/inventory`),
  allocate: (productId, platformId, allocatedQuantity) =>
    api.post(`/products/${productId}/inventory`, { platformId, allocatedQuantity }),
  update: (productId, platformId, allocatedQuantity) =>
    api.put(`/products/${productId}/inventory/${platformId}`, { allocatedQuantity }),
  remove: (productId, platformId) =>
    api.delete(`/products/${productId}/inventory/${platformId}`),
};