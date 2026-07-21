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
      <h3 className="text-lg font-semibold text-slate-900">Categories</h3>

      <form onSubmit={handleAdd} className="mt-3">
        <div className="flex gap-2">
          <input
            type="text"
            name="newCategory"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="New category name"
            required
            className="flex-1 rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
          <button
            type="submit"
            disabled={addLoading}
            className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {addLoading ? "Adding..." : "Add"}
          </button>
        </div>
        {addError && <p className="mt-1 text-sm text-red-600">{addError}</p>}
      </form>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}
      {editError && <p className="mt-2 text-sm text-red-600">{editError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading categories...</p>
      ) : categories.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No categories yet.</p>
      ) : (
        <ul className="mt-3 divide-y divide-slate-100">
          {categories.map((category) => (
            <li key={category.id} className="flex items-center gap-2 py-2">
              {editingId === category.id ? (
                <>
                  <input
                    type="text"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    className="flex-1 rounded-md border border-slate-300 px-2 py-1.5 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                  />
                  <button
                    onClick={() => handleEditSave(category.id)}
                    disabled={editLoading}
                    className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:opacity-60"
                  >
                    {editLoading ? "Saving..." : "Save"}
                  </button>
                  <button
                    onClick={cancelEdit}
                    disabled={editLoading}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <span className="flex-1 text-sm text-slate-800">{category.name}</span>
                  <button
                    onClick={() => startEdit(category)}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(category.id)}
                    disabled={deletingId === category.id}
                    className="rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 disabled:opacity-60"
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
