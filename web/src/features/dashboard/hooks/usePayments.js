import { useState, useEffect } from "react";
import api from "../../../core/api/axios";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

export function usePayments() {
  const [payments, setPayments] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const fetchPayments = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await api.get("/admin/payments");
      setPayments(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load payment records."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, []);

  return { payments, loadingList, listError };
}