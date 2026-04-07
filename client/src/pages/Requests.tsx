import { useEffect, useState, useCallback } from "react";
import {
  fetchConnectionRequests,
  fetchUserSummary,
  acceptConnectionRequest,
  dismissConnectionRequest,
} from "../api/client";
import type { UserSummary } from "../api/types";
import Avatar from "../components/Avatar";

export default function Requests() {
  const [items, setItems] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actingOn, setActingOn] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const { ids } = await fetchConnectionRequests();
      const summaries = await Promise.all(ids.map(fetchUserSummary));
      setItems(summaries);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function handleAccept(userId: number) {
    setActingOn(userId);
    try {
      await acceptConnectionRequest(userId);
      setItems((prev) => prev.filter((u) => u.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to accept");
    } finally {
      setActingOn(null);
    }
  }

  async function handleDismiss(userId: number) {
    setActingOn(userId);
    try {
      await dismissConnectionRequest(userId);
      setItems((prev) => prev.filter((u) => u.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to dismiss");
    } finally {
      setActingOn(null);
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <div>
          <h1>Connection Requests</h1>
          <p className="subtitle">People who want to connect with you.</p>
        </div>
      </div>

      {loading && <p className="muted">Loading requests…</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No pending connection requests.</p>
      )}

      <ul className="user-list">
        {items.map((item) => (
          <li className="user-list-item" key={item.id}>
            <Avatar name={item.name} url={item.profilePictureUrl} />
            <div className="user-info">
              <h3>{item.name ?? `User ${item.id}`}</h3>
            </div>
            <div className="user-actions">
              <button
                className="primary-button"
                type="button"
                disabled={actingOn !== null}
                onClick={() => handleAccept(item.id)}
              >
                {actingOn === item.id ? "Accepting…" : "Accept"}
              </button>
              <button
                className="ghost-button"
                type="button"
                disabled={actingOn !== null}
                onClick={() => handleDismiss(item.id)}
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
