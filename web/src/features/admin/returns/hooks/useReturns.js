import { useState, useEffect } from "react";
import { fetchReturns } from "../api/returnsApi";
import { getErrorMessage } from "../../../../core/api/getErrorMessage";

export function useReturns() {
  const [returns, setReturns] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const loadReturns = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await fetchReturns();
      setReturns(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load return records."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadReturns();
  }, []);

  return { returns, loadingList, listError };
}
