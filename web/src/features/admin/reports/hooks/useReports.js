import { useState, useEffect } from "react";
import { fetchReports } from "../api/reportsApi";
import { getErrorMessage } from "../../../../core/api/getErrorMessage";

export function useReports() {
  const [reports, setReports] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const loadReports = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await fetchReports();
      setReports(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load vendor reports."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadReports();
  }, []);

  return { reports, loadingList, listError };
}
