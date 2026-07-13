import api from "../../../core/api/axios";

export const platformApi = {
  list: () => api.get("/platforms"),
  create: (name) => api.post("/platforms", { name }),
  update: (id, name) => api.put(`/platforms/${id}`, { name }),
  remove: (id) => api.delete(`/platforms/${id}`),
};