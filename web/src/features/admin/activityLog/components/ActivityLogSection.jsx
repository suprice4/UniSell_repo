import { useActivityLog } from "../hooks/useActivityLog";

function ActivityLogSection() {
  const { entries, loadingList, listError } = useActivityLog();

  return (
    <div>
      <h3>Activity Log</h3>

      {listError && <p style={{ color: "red" }}>{listError}</p>}

      {loadingList ? (
        <p>Loading activity log...</p>
      ) : entries.length === 0 ? (
        <p>No activity recorded yet.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {entries.map((e) => (
            <li
              key={e.id}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "8px 0",
                borderBottom: "1px solid #eee",
              }}
            >
              <span style={{ flex: 1 }}>{e.actorEmail}</span>
              <span style={{ flex: 1 }}>{e.actorRole}</span>
              <span style={{ flex: 1 }}>{e.actionType}</span>
              <span style={{ flex: 2 }}>{e.description}</span>
              <span style={{ flex: 1 }}>
                {new Date(e.timestamp).toLocaleString()}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ActivityLogSection;