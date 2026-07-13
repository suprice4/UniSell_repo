import api from "../../../core/api/axios";

export const productApi = {
  list: () => api.get("/products"),
  create: (payload) => api.post("/products", payload),
  update: (id, payload) => api.put(`/products/${id}`, payload),
  remove: (id) => api.delete(`/products/${id}`),
};