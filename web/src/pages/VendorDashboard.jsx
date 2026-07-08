import { useState, useEffect } from "react";
import api from "../api/axios";

function VendorDashboard() {
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

  const getErrorMessage = (err, fallback) => {
    const data = err.response?.data;
    return typeof data === "string" ? data : data?.message || fallback;
  };

  const fetchCategories = async () => {
    setListError("");
    setLoadingList(true);
    try {
      const res = await api.get("/categories");
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
      const res = await api.post("/categories", { name: newName });
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
      const res = await api.put(`/categories/${id}`, { name: editName });
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
      await api.delete(`/categories/${id}`);
      setCategories(categories.filter((c) => c.id !== id));
    } catch (err) {
      setListError(getErrorMessage(err, "Failed to delete category."));
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div style={{ maxWidth: "500px", margin: "60px auto", fontFamily: "sans-serif" }}>
      <h2>Vendor Dashboard</h2>

      <h3>Categories</h3>

      <form onSubmit={handleAdd} style={{ marginBottom: "16px" }}>
        <div style={{ display: "flex", gap: "8px" }}>
          <input
            type="text"
            name="newCategory"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="New category name"
            required
            style={{ flex: 1, padding: "8px" }}
          />
          <button type="submit" disabled={addLoading} style={{ padding: "8px 16px" }}>
            {addLoading ? "Adding..." : "Add"}
          </button>
        </div>
        {addError && <p style={{ color: "red" }}>{addError}</p>}
      </form>

      {listError && <p style={{ color: "red" }}>{listError}</p>}
      {editError && <p style={{ color: "red" }}>{editError}</p>}

      {loadingList ? (
        <p>Loading categories...</p>
      ) : categories.length === 0 ? (
        <p>No categories yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {categories.map((category) => (
            <li
              key={category.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              {editingId === category.id ? (
                <>
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    style={{ flex: 1, padding: "6px" }}
                  />
                  <button
                    onClick={() => handleEditSave(category.id)}
                    disabled={editLoading}
                    style={{ padding: "6px 12px" }}
                  >
                    {editLoading ? "Saving..." : "Save"}
                  </button>
                  <button onClick={cancelEdit} disabled={editLoading} style={{ padding: "6px 12px" }}>
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <span style={{ flex: 1 }}>{category.name}</span>
                  <button onClick={() => startEdit(category)} style={{ padding: "6px 12px" }}>
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(category.id)}
                    disabled={deletingId === category.id}
                    style={{ padding: "6px 12px" }}
                  >
                    {deletingId === category.id ? "Deleting..." : "Delete"}
                  </button>
                </>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default VendorDashboard;