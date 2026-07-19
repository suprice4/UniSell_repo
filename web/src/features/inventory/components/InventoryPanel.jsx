import { isLowStockAllocation } from "../../product/utils/lowStock";

function InventoryPanel({
  productId,
  product,
  platforms,
  allocations,
  loading,
  allocPlatformId,
  setAllocPlatformId,
  allocQuantity,
  setAllocQuantity,
  allocLoading,
  allocError,
  onAllocate,
  onDeleteAllocation,
  editPlatformId,
  editQuantity,
  setEditQuantity,
  editLoading,
  editError,
  onStartEdit,
  onCancelEdit,
  onUpdateAllocation,
}) {
  return (
    <div
      style={{
        marginTop: "8px",
        marginLeft: "16px",
        padding: "8px",
        background: "#fafafa",
        border: "1px solid #eee",
      }}
    >
      <strong style={{ fontSize: "0.9em" }}>Platform allocations</strong>

      {loading ? (
        <p style={{ fontSize: "0.9em" }}>Loading...</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0, marginTop: "6px" }}>
          {(allocations || []).length === 0 && (
            <li style={{ fontSize: "0.9em", color: "#888" }}>No allocations yet.</li>
          )}
          {(allocations || []).map((alloc) => {
            const isEditing = editPlatformId === alloc.platformId;
            return (
              <li
                key={alloc.id}
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "4px",
                  fontSize: "0.9em",
                  padding: "4px 0",
                }}
              >
                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  {isEditing ? (
                    <>
                      <span style={{ flex: 1 }}>{alloc.platformName}:</span>
                      <input
                        type="number"
                        value={editQuantity}
                        onChange={(e) => setEditQuantity(e.target.value)}
                        style={{ padding: "4px", width: "70px" }}
                        autoFocus
                      />
                      <button
                        onClick={() =>
                          onUpdateAllocation(productId, alloc.platformId, editQuantity)
                        }
                        disabled={editLoading}
                        style={{ padding: "4px 8px", fontSize: "0.85em" }}
                      >
                        {editLoading ? "..." : "Save"}
                      </button>
                      <button
                        onClick={onCancelEdit}
                        disabled={editLoading}
                        style={{ padding: "4px 8px", fontSize: "0.85em" }}
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      <span style={{ flex: 1 }}>
                        {alloc.platformName}: {alloc.allocatedQuantity}
                        {isLowStockAllocation(alloc, product) && (
                          <span
                            style={{
                              marginLeft: "8px",
                              padding: "2px 8px",
                              borderRadius: "4px",
                              fontSize: "12px",
                              fontWeight: "bold",
                              color: "#fff",
                              backgroundColor: "#c0392b",
                            }}
                          >
                            LOW STOCK
                          </span>
                        )}
                      </span>
                      <button
                        onClick={() => onStartEdit(alloc.platformId, alloc.allocatedQuantity)}
                        style={{ padding: "4px 8px", fontSize: "0.85em" }}
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onDeleteAllocation(productId, alloc.platformId)}
                        style={{ padding: "4px 8px", fontSize: "0.85em" }}
                      >
                        Remove
                      </button>
                    </>
                  )}
                </div>
                {isEditing && editError && (
                  <p style={{ color: "red", fontSize: "0.85em", margin: 0 }}>{editError}</p>
                )}
              </li>
            );
          })}
        </ul>
      )}

      <form
        onSubmit={(e) => onAllocate(e, productId)}
        style={{ display: "flex", gap: "6px", marginTop: "8px" }}
      >
        <select
          value={allocPlatformId}
          onChange={(e) => setAllocPlatformId(e.target.value)}
          required
          style={{ padding: "6px", flex: 1 }}
        >
          <option value="" disabled>
            Select platform
          </option>
          {platforms.map((platform) => (
            <option key={platform.id} value={platform.id}>
              {platform.name}
            </option>
          ))}
        </select>
        <input
          type="number"
          value={allocQuantity}
          onChange={(e) => setAllocQuantity(e.target.value)}
          placeholder="Qty"
          required
          style={{ padding: "6px", width: "80px" }}
        />
        <button type="submit" disabled={allocLoading} style={{ padding: "6px 12px" }}>
          {allocLoading ? "..." : "Allocate"}
        </button>
      </form>
      {allocError && (
        <p style={{ color: "red", fontSize: "0.9em", marginTop: "4px" }}>{allocError}</p>
      )}
    </div>
  );
}

export default InventoryPanel;