import { useState, useEffect } from "react";
import { fetchPayments } from "../api/paymentsApi";
import { getErrorMessage } from "../../../../core/api/getErrorMessage";

export function usePayments() {
  const [payments, setPayments] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const loadPayments = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await fetchPayments();
      setPayments(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load payment records."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadPayments();
  }, []);

  return { payments, loadingList, listError };
}
