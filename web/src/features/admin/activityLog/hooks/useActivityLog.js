import { useState, useEffect } from "react";
import { fetchActivityLog } from "../api/activityLogApi";
import { getErrorMessage } from "../../../../core/api/getErrorMessage";

export function useActivityLog() {
  const [entries, setEntries] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const fetchEntries = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await fetchActivityLog();
      setEntries(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load activity log."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchEntries();
  }, []);

  return { entries, loadingList, listError };
}