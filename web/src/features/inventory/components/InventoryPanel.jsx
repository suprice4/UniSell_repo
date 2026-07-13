function InventoryPanel({
  productId,
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
          {(allocations || []).map((alloc) => (
            <li
              key={alloc.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                fontSize: "0.9em",
                padding: "4px 0",
              }}
            >
              <span style={{ flex: 1 }}>
                {alloc.platformName}: {alloc.allocatedQuantity}
              </span>
              <button
                onClick={() => onDeleteAllocation(productId, alloc.platformId)}
                style={{ padding: "4px 8px", fontSize: "0.85em" }}
              >
                Remove
              </button>
            </li>
          ))}
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