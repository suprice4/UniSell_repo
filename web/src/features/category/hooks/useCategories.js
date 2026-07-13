import { useState, useEffect } from "react";
import { categoryApi } from "../api/categoryApi";

const getErrorMessage = (err, fallback) => {
  const data = err.response?.data;
  return typeof data === "string" ? data : data?.message || fallback;
};

export function useCategories() {
  const [categories, setCategories] = useState([]);
  const [loadingList, setLoadingList] = useState(true);
  const [listError, setListError] = useState("");

  const [newName, setNewName] = useState("");
  const [addLoading, setAddLoading] = useState(false);
  const [addError, setAddError] = useState("");

  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState("");
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState("");

  const [deletingId, setDeletingId] = useState(null);

  const fetchCategories = async () => {
    setListError("");
    setLoadingList(true);
    try {
      const res = await categoryApi.list();
      setCategories(res.data);
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to load categories."));
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    setAddError("");
    setAddLoading(true);
    try {
      const res = await categoryApi.create(newName);
      setCategories([...categories, res.data]);
      setNewName("");
    } catch (err) {
      setAddError(getErrorMessage(err, "Failed to add category."));
    } finally {
      setAddLoading(false);
    }
  };

  const startEdit = (category) => {
    setEditingId(category.id);
    setEditName(category.name);
    setEditError("");
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditName("");
    setEditError("");
  };

  const handleEditSave = async (id) => {
    setEditError("");
    setEditLoading(true);
    try {
      const res = await categoryApi.update(id, editName);
      setCategories(categories.map((c) => (c.id === id ? res.data : c)));
      setEditingId(null);
      setEditName("");
    } catch (err) {
      setEditError(getErrorMessage(err, "Failed to update category."));
    } finally {
      setEditLoading(false);
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm("Are you sure you want to delete this category?");
    if (!confirmed) return;
    setListError("");
    setDeletingId(id);
    try {
      await categoryApi.remove(id);
      setCategories(categories.filter((c) => c.id !== id));
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to delete category."));
    } finally {
      setDeletingId(null);
    }
  };

  return {
    categories,
    loadingList,
    listError,
    newName,
    setNewName,
    addLoading,
    addError,
    editingId,
    editName,
    setEditName,
    editLoading,
    editError,
    deletingId,
    handleAdd,
    startEdit,
    cancelEdit,
    handleEditSave,
    handleDelete,
  };
}