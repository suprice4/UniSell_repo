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
    <div className="mt-2 ml-4 rounded-md border border-slate-200 bg-slate-50 p-3">
      <strong className="text-sm text-slate-700">Platform allocations</strong>

      {loading ? (
        <p className="mt-1 text-sm text-slate-500">Loading...</p>
      ) : (
        <ul className="mt-1.5 space-y-1">
          {(allocations || []).length === 0 && (
            <li className="text-sm text-slate-400">No allocations yet.</li>
          )}
          {(allocations || []).map((alloc) => {
            const isEditing = editPlatformId === alloc.platformId;
            return (
              <li key={alloc.id} className="flex flex-col gap-1 py-1 text-sm">
                <div className="flex items-center gap-2">
                  {isEditing ? (
                    <>
                      <span className="flex-1 text-slate-700">{alloc.platformName}:</span>
                      <input
                        type="number"
                        value={editQuantity}
                        onChange={(e) => setEditQuantity(e.target.value)}
                        className="w-20 rounded-md border border-slate-300 px-2 py-1 text-sm"
                        autoFocus
                      />
                      <button
                        onClick={() =>
                          onUpdateAllocation(productId, alloc.platformId, editQuantity)
                        }
                        disabled={editLoading}
                        className="rounded-md bg-indigo-600 px-2 py-1 text-xs font-medium text-white transition hover:bg-indigo-700 disabled:opacity-60"
                      >
                        {editLoading ? "..." : "Save"}
                      </button>
                      <button
                        onClick={onCancelEdit}
                        disabled={editLoading}
                        className="rounded-md border border-slate-300 px-2 py-1 text-xs font-medium text-slate-700 transition hover:bg-white disabled:opacity-60"
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      <span className="flex-1 text-slate-700">
                        {alloc.platformName}: {alloc.allocatedQuantity}
                        {isLowStockAllocation(alloc, product) && (
                          <span className="ml-2 rounded-md bg-red-600 px-2 py-0.5 text-xs font-bold text-white">
                            LOW STOCK
                          </span>
                        )}
                      </span>
                      <button
                        onClick={() => onStartEdit(alloc.platformId, alloc.allocatedQuantity)}
                        className="rounded-md border border-slate-300 px-2 py-1 text-xs font-medium text-slate-700 transition hover:bg-white"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onDeleteAllocation(productId, alloc.platformId)}
                        className="rounded-md border border-red-200 px-2 py-1 text-xs font-medium text-red-600 transition hover:bg-red-50"
                      >
                        Remove
                      </button>
                    </>
                  )}
                </div>
                {isEditing && editError && <p className="text-xs text-red-600">{editError}</p>}
              </li>
            );
          })}
        </ul>
      )}

      <form onSubmit={(e) => onAllocate(e, productId)} className="mt-2 flex gap-1.5">
        <select
          value={allocPlatformId}
          onChange={(e) => setAllocPlatformId(e.target.value)}
          required
          className="flex-1 rounded-md border border-slate-300 px-2 py-1.5 text-sm"
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
          className="w-20 rounded-md border border-slate-300 px-2 py-1.5 text-sm"
        />
        <button
          type="submit"
          disabled={allocLoading}
          className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-700 disabled:opacity-60"
        >
          {allocLoading ? "..." : "Allocate"}
        </button>
      </form>
      {allocError && <p className="mt-1 text-sm text-red-600">{allocError}</p>}
    </div>
  );
}

export default InventoryPanel;
