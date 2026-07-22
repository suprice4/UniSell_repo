import { useActivityLog } from "../hooks/useActivityLog";

function ActivityLogSection() {
  const { entries, loadingList, listError } = useActivityLog();

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-slate-900">Activity Log</h3>

      {listError && <p className="mt-2 text-sm text-red-600">{listError}</p>}

      {loadingList ? (
        <p className="mt-3 text-sm text-slate-500">Loading activity log...</p>
      ) : entries.length === 0 ? (
        <p className="mt-3 text-sm text-slate-500">No activity recorded yet.</p>
      ) : (
        <div className="mt-3">
          <div className="flex items-center gap-2 border-b border-slate-200 pb-2 text-xs font-medium uppercase tracking-wide text-slate-500">
            <span className="flex-1">Actor</span>
            <span className="flex-1">Role</span>
            <span className="flex-1">Action</span>
            <span className="flex-[2]">Description</span>
            <span className="flex-1">Timestamp</span>
          </div>
          <ul className="divide-y divide-slate-100">
            {entries.map((e) => (
              <li key={e.id} className="flex items-center gap-2 py-2.5">
                <span className="flex-1 text-sm text-slate-800">{e.actorEmail}</span>
                <span className="flex-1 text-sm text-slate-600">{e.actorRole}</span>
                <span className="flex-1 text-sm text-slate-600">{e.actionType}</span>
                <span className="flex-[2] text-sm text-slate-700">{e.description}</span>
                <span className="flex-1 text-sm text-slate-500">
                  {new Date(e.timestamp).toLocaleString()}
                </span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default ActivityLogSection;
