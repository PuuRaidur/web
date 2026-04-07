import { useEffect, useState, useCallback } from "react";
import {
  fetchMe,
  fetchOutgoingConnectionRequests,
  fetchRecommendations,
  fetchUserSummary,
  sendConnectionRequest,
  dismissRecommendation,
} from "../api/client";
import type { UserSummary } from "../api/types";
import Avatar from "../components/Avatar";

const MAX_RECOMMENDATIONS = 10;

export default function Recommendations() {
  const [items, setItems] = useState<UserSummary[]>([]);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actingOn, setActingOn] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const me = await fetchMe();
      const outgoing = await fetchOutgoingConnectionRequests();
      const { ids } = await fetchRecommendations();

      // Filter out current user and users we already sent requests to
      const filteredIds = ids
        .filter((id) => id !== me.id && !outgoing.ids.includes(id))
        .slice(0, MAX_RECOMMENDATIONS);

      const summaries = await Promise.all(filteredIds.map(fetchUserSummary));
      setItems(summaries);
      setCurrentUserId(me.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function handleConnect(userId: number) {
    if (currentUserId && userId === currentUserId) return;
    setActingOn(userId);
    try {
      await sendConnectionRequest(userId);
      setItems((prev) => prev.filter((u) => u.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to connect");
    } finally {
      setActingOn(null);
    }
  }

  async function handleDismiss(userId: number) {
    setActingOn(userId);
    try {
      await dismissRecommendation(userId);
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
          <h1>Recommendations</h1>
          <p className="subtitle">
            Up to {MAX_RECOMMENDATIONS} people you might want to connect with.
          </p>
        </div>
        <button className="ghost-button" type="button" onClick={load}>
          Refresh
        </button>
      </div>

      {loading && <p className="muted">Loading recommendations…</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No recommendations right now.</p>
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
                onClick={() => handleConnect(item.id)}
              >
                {actingOn === item.id ? "Sending…" : "Connect"}
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
