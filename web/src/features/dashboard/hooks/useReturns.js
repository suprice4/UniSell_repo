import { useState, useEffect } from "react";
import api from "../../../core/api/axios";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

export function useReturns() {
  const [returns, setReturns] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const fetchReturns = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await api.get("/admin/returns");
      setReturns(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load return records."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchReturns();
  }, []);

  return { returns, loadingList, listError };
}