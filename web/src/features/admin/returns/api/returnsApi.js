import api from "../../../../core/api/axios";

export function fetchReturns() {
  return api.get("/admin/returns");
}
