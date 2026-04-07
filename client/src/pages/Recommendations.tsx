import { useEffect, useState, useCallback } from "react";
import {
  getRecommendations,
  getUserSummaries,
  sendConnectionRequest,
  dismissRecommendation,
  type UserSummary,
} from "../api";

export default function Recommendations() {
  const [users, setUsers] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [acting, setActing] = useState<number | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const recs = await getRecommendations();
      if (recs.ids.length === 0) {
        setUsers([]);
        return;
      }
      const summaries = await getUserSummaries(recs.ids);
      setUsers(summaries);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to load recommendations";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleConnect = async (userId: number) => {
    setActing(userId);
    try {
      await sendConnectionRequest(userId);
      // Remove from list after successful request
      setUsers((prev) => prev.filter((u) => u.id !== userId));
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to send request";
      setError(msg);
    } finally {
      setActing(null);
    }
  };

  const handleDismiss = async (userId: number) => {
    setActing(userId);
    try {
      await dismissRecommendation(userId);
      setUsers((prev) => prev.filter((u) => u.id !== userId));
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to dismiss";
      setError(msg);
    } finally {
      setActing(null);
    }
  };

  if (loading) return <p>Loading recommendations…</p>;
  if (error) return <p className="error-text">{error}</p>;
  if (users.length === 0) return <p>No recommendations available right now.</p>;

  return (
    <section className="page">
      <div className="page-head">
        <div>
          <h1>Recommendations</h1>
          <p className="subtitle">Up to 10 people you might want to connect with.</p>
        </div>
        <button className="ghost-button" type="button" onClick={load}>
          Refresh
        </button>
      </div>

      <ul className="user-list">
        {users.map((user) => (
          <li className="user-list-item" key={user.id}>
            <div className="avatar">
              {user.profilePictureUrl ? (
                <img src={user.profilePictureUrl} alt={user.name || "User"} />
              ) : (
                initials(user.name)
              )}
            </div>
            <div className="user-info">
              <h3>{user.name || `User ${user.id}`}</h3>
            </div>
            <div className="user-actions">
              <button
                className="primary-button"
                type="button"
                disabled={acting !== null}
                onClick={() => handleConnect(user.id)}
              >
                {acting === user.id ? "Sending…" : "Connect"}
              </button>
              <button
                className="ghost-button"
                type="button"
                disabled={acting !== null}
                onClick={() => handleDismiss(user.id)}
              >
                Dismiss
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
