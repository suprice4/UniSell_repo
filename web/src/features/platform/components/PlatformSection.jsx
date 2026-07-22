import { usePlatforms } from "../hooks/usePlatforms";

function PlatformSection() {
  const {
    platforms,
    loadingPlatforms,
    platformListError,
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
  } = usePlatforms();

  return (
    <div>
      <h3 className="text-lg font-semibold text-slate-900">Platforms</h3>

      <form onSubmit={handleAddPlatform} className="mt-3">
        <div className="flex gap-2">
          <input
            type="text"
            value={newPlatformName}
            onChange={(e) => setNewPlatformName(e.target.value)}
            placeholder="New platform name"
            required
            className="flex-1 rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
          <button
            type="submit"
            disabled={addPlatformLoading}
            className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {addPlatformLoading ? "Adding..." : "Add"}
          </button>
        </div>
        {addPlatformError && <p className="mt-1 text-sm text-red-600">{addPlatformError}</p>}
      </form>

      {platformListError && <p className="mt-2 text-sm text-red-600">{platformListError}</p>}
      {editPlatformError && <p className="mt-2 text-sm text-red-600">{editPlatformError}</p>}

      {loadingPlatforms ? (
        <p className="mt-3 text-sm text-slate-500">Loading platforms...</p>
      ) : platforms.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No platforms yet.</p>
      ) : (
        <div className="mt-3">
        <div className="flex items-center gap-2 border-b border-slate-200 pb-2 text-xs font-medium uppercase tracking-wide text-slate-500">
          <span className="flex-1">Name</span>
          <span>Actions</span>
        </div>
        <ul className="divide-y divide-slate-100">
          {platforms.map((platform) => (
            <li key={platform.id} className="flex items-center gap-2 py-2">
              {editingPlatformId === platform.id ? (
                <>
                  <input
                    type="text"
                    value={editPlatformName}
                    onChange={(e) => setEditPlatformName(e.target.value)}
                    className="flex-1 rounded-md border border-slate-300 px-2 py-1.5 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                  />
                  <button
                    onClick={() => handleEditPlatformSave(platform.id)}
                    disabled={editPlatformLoading}
                    className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:opacity-60"
                  >
                    {editPlatformLoading ? "Saving..." : "Save"}
                  </button>
                  <button
                    onClick={cancelEditPlatform}
                    disabled={editPlatformLoading}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-60"
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <span className="flex-1 text-sm text-slate-800">{platform.name}</span>
                  <button
                    onClick={() => startEditPlatform(platform)}
                    className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeletePlatform(platform.id)}
                    disabled={deletingPlatformId === platform.id}
                    className="rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 disabled:opacity-60"
                  >
                    {deletingPlatformId === platform.id ? "Deleting..." : "Delete"}
                  </button>
                </>
              )}
            </li>
          ))}
        </ul>
        </div>
      )}
    </div>
  );
}

export default PlatformSection;
