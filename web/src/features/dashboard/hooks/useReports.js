import { useState, useEffect } from "react";
import api from "../../../core/api/axios";
import { getErrorMessage } from "../../../core/api/getErrorMessage";

export function useReports() {
  const [reports, setReports] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const fetchReports = async () => {
    setLoadingList(true);
    setListError("");
    try {
      const res = await api.get("/admin/reports");
      setReports(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load vendor reports."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, []);

  return { reports, loadingList, listError };
}