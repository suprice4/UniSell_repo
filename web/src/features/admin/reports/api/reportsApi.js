import api from "../../../../core/api/axios";

export function fetchReports() {
  return api.get("/admin/reports");
}
