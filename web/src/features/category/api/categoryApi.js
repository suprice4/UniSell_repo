import api from "../../../core/api/axios";

export const categoryApi = {
  list: () => api.get("/categories"),
  create: (name) => api.post("/categories", { name }),
  update: (id, name) => api.put(`/categories/${id}`, { name }),
  remove: (id) => api.delete(`/categories/${id}`),
};