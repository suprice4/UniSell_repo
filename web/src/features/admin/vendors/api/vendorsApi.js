import api from "../../../../core/api/axios";

export function fetchVendors() {
  return api.get("/admin/vendors");
}

export function updateVendorStatus(vendorId, enabled) {
  return api.put(`/admin/vendors/${vendorId}/status`, { enabled });
}
