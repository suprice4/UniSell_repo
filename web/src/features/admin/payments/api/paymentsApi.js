import api from "../../../../core/api/axios";

export function fetchPayments() {
  return api.get("/admin/payments");
}
