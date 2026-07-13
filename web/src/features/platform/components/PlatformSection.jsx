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
      <h3 style={{ marginTop: "32px" }}>Platforms</h3>

      <form onSubmit={handleAddPlatform} style={{ marginBottom: "16px" }}>
        <div style={{ display: "flex", gap: "8px" }}>
          <input
            type="text"
            value={newPlatformName}
            onChange={(e) => setNewPlatformName(e.target.value)}
            placeholder="New platform name"
            required
            style={{ flex: 1, padding: "8px" }}
          />
          <button type="submit" disabled={addPlatformLoading} style={{ padding: "8px 16px" }}>
            {addPlatformLoading ? "Adding..." : "Add"}
          </button>
        </div>
        {addPlatformError && <p style={{ color: "red" }}>{addPlatformError}</p>}
      </form>

      {platformListError && <p style={{ color: "red" }}>{platformListError}</p>}
      {editPlatformError && <p style={{ color: "red" }}>{editPlatformError}</p>}

      {loadingPlatforms ? (
        <p>Loading platforms...</p>
      ) : platforms.length === 0 ? (
        <p>No platforms yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {platforms.map((platform) => (
            <li
              key={platform.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              {editingPlatformId === platform.id ? (
                <>
                  <input
                    type="text"
                    value={editPlatformName}
                    onChange={(e) => setEditPlatformName(e.target.value)}
                    style={{ flex: 1, padding: "6px" }}
                  />
                  <button
                    onClick={() => handleEditPlatformSave(platform.id)}
                    disabled={editPlatformLoading}
                    style={{ padding: "6px 12px" }}
                  >
                    {editPlatformLoading ? "Saving..." : "Save"}
                  </button>
                  <button
                    onClick={cancelEditPlatform}
                    disabled={editPlatformLoading}
                    style={{ padding: "6px 12px" }}
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <span style={{ flex: 1 }}>{platform.name}</span>
                  <button onClick={() => startEditPlatform(platform)} style={{ padding: "6px 12px" }}>
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeletePlatform(platform.id)}
                    disabled={deletingPlatformId === platform.id}
                    style={{ padding: "6px 12px" }}
                  >
                    {deletingPlatformId === platform.id ? "Deleting..." : "Delete"}
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

export default PlatformSection;