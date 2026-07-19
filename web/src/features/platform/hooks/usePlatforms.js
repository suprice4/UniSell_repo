import { useState } from "react";
import { platformApi } from "../api/platformApi";
import { getErrorMessage } from "../../../core/api/getErrorMessage";
import { useDashboard } from "../../dashboard/context/DashboardContext";

export function usePlatforms() {
  const { platforms, setPlatforms, loadingPlatforms, platformsError } = useDashboard();

  const [newPlatformName, setNewPlatformName] = useState("");
  const [addPlatformLoading, setAddPlatformLoading] = useState(false);
  const [addPlatformError, setAddPlatformError] = useState("");

  const [deletingPlatformId, setDeletingPlatformId] = useState(null);
  const [deletePlatformError, setDeletePlatformError] = useState("");

  const [editingPlatformId, setEditingPlatformId] = useState(null);
  const [editPlatformName, setEditPlatformName] = useState("");
  const [editPlatformLoading, setEditPlatformLoading] = useState(false);
  const [editPlatformError, setEditPlatformError] = useState("");

  const handleAddPlatform = async (e) => {
    e.preventDefault();
    setAddPlatformError("");
    setAddPlatformLoading(true);
    try {
      const res = await platformApi.create(newPlatformName);
      setPlatforms([...platforms, res.data]);
      setNewPlatformName("");
    } catch (err) {
      setAddPlatformError(getErrorMessage(err, "Failed to add platform."));
    } finally {
      setAddPlatformLoading(false);
    }
  };

  const startEditPlatform = (platform) => {
    setEditingPlatformId(platform.id);
    setEditPlatformName(platform.name);
    setEditPlatformError("");
  };

  const cancelEditPlatform = () => {
    setEditingPlatformId(null);
    setEditPlatformName("");
    setEditPlatformError("");
  };

  const handleEditPlatformSave = async (id) => {
    setEditPlatformError("");
    setEditPlatformLoading(true);
    try {
      const res = await platformApi.update(id, editPlatformName);
      setPlatforms(platforms.map((p) => (p.id === id ? res.data : p)));
      setEditingPlatformId(null);
      setEditPlatformName("");
    } catch (err) {
      setEditPlatformError(getErrorMessage(err, "Failed to update platform."));
    } finally {
      setEditPlatformLoading(false);
    }
  };

  const handleDeletePlatform = async (id) => {
    const confirmed = window.confirm("Are you sure you want to delete this platform?");
    if (!confirmed) return;
    setDeletePlatformError("");
    setDeletingPlatformId(id);
    try {
      await platformApi.remove(id);
      setPlatforms(platforms.filter((p) => p.id !== id));
    } catch (err) {
      setDeletePlatformError(getErrorMessage(err, "Failed to delete platform."));
    } finally {
      setDeletingPlatformId(null);
    }
  };

  return {
    platforms,
    loadingPlatforms,
    platformListError: platformsError || deletePlatformError,
    newPlatformName,
    setNewPlatformName,
    addPlatformLoading,
    addPlatformError,
    deletingPlatformId,
    editingPlatformId,
    editPlatformName,
    setEditPlatformName,
    editPlatformLoading,
    editPlatformError,
    handleAddPlatform,
    startEditPlatform,
    cancelEditPlatform,
    handleEditPlatformSave,
    handleDeletePlatform,
  };
}