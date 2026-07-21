import { useState, useEffect } from "react";
import { fetchVendors, updateVendorStatus } from "../api/vendorsApi";
import { getErrorMessage } from "../../../../core/api/getErrorMessage";

export function useVendors() {
  const [vendors, setVendors] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");
  const [togglingId, setTogglingId] = useState(null);
  const [toggleError, setToggleError] = useState("");

  const loadVendors = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await fetchVendors();
      setVendors(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load vendors."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadVendors();
  }, []);

  const handleToggle = async (vendor) => {
    setTogglingId(vendor.id);
    setToggleError("");
    try {
      const res = await updateVendorStatus(vendor.id, !vendor.enabled);
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
