import { useEffect, useState, useCallback } from "react";
import {
  getConnections,
  getUserSummaries,
  disconnect,
  type ConnectionDetail,
  type UserSummary,
} from "../api";

type ConnectionWithUser = ConnectionDetail & { user: UserSummary };

export default function Connections() {
  const [connections, setConnections] = useState<ConnectionWithUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [acting, setActing] = useState<number | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const conns = await getConnections();
      if (conns.length === 0) {
        setConnections([]);
        return;
      }
      const summaries = await getUserSummaries(conns.map((c) => c.otherUserId));
      const merged = conns.map((conn) => ({
        ...conn,
        user: summaries.find((s) => s.id === conn.otherUserId)!,
      }));
      setConnections(merged);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to load connections";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleDisconnect = async (connectionId: number) => {
    setActing(connectionId);
    try {
      await disconnect(connectionId);
      setConnections((prev) => prev.filter((c) => c.connectionId !== connectionId));
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to disconnect";
      setError(msg);
    } finally {
      setActing(null);
    }
  };

  if (loading) return <p>Loading connections…</p>;
  if (error) return <p className="error-text">{error}</p>;
  if (connections.length === 0) return <p>No connections yet.</p>;

  return (
    <section className="page">
      <div className="page-head">
        <div>
          <h1>Connections</h1>
          <p className="subtitle">People you are connected with.</p>
        </div>
      </div>

      <ul className="user-list">
        {connections.map((conn) => (
          <li className="user-list-item" key={conn.connectionId}>
            <div className="avatar">
              {conn.user.profilePictureUrl ? (
                <img src={conn.user.profilePictureUrl} alt={conn.user.name || "User"} />
              ) : (
                initials(conn.user.name)
              )}
            </div>
            <div className="user-info">
              <h3>{conn.user.name || `User ${conn.user.id}`}</h3>
            </div>
            <div className="user-actions">
              <button
                className="danger-button"
                type="button"
                disabled={acting !== null}
                onClick={() => handleDisconnect(conn.connectionId)}
              >
                {acting === conn.connectionId ? "Disconnecting…" : "Disconnect"}
              </button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}

function initials(name: string | null): string {
  if (!name) return "??";
  return name
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0])
    .join("")
    .toUpperCase();
}
