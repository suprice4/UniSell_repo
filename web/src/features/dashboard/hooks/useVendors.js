import { useState, useEffect } from "react";
import api from "../../../core/api/axios";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

export function useVendors() {
  const [vendors, setVendors] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");
  const [togglingId, setTogglingId] = useState(null);
  const [toggleError, setToggleError] = useState("");

  const fetchVendors = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await api.get("/admin/vendors");
      setVendors(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load vendors."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchVendors();
  }, []);

  const handleToggle = async (vendor) => {
    setTogglingId(vendor.id);
    setToggleError("");
    try {
      const res = await api.put(`/admin/vendors/${vendor.id}/status`, {
        enabled: !vendor.enabled,
      });
      setVendors((prev) =>
        prev.map((v) => (v.id === vendor.id ? res.data : v))
      );
    } catch (err) {
      setToggleError(getErrorMessage(err, "Failed to update vendor status."));
    } finally {
      setTogglingId(null);
    }
  };

  return {
    vendors,
    loadingList,
    listError,
    togglingId,
    toggleError,
    handleToggle,
  };
}