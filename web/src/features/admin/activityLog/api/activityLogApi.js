import api from "../../../../core/api/axios";

export function fetchActivityLog() {
  return api.get("/admin/activity-log");
}