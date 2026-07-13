import { useCategories } from "../hooks/useCategories";

function CategorySection() {
  const {
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
  } = useCategories();

  return (
    <div>
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

export default CategorySection;