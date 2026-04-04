import { useEffect, useState } from "react";
import {
  acceptConnectionRequest,
  dismissConnectionRequest,
  fetchConnectionRequests,
  fetchUserSummary,
} from "../api/client";
import type { UserSummary } from "../api/types";

export default function Requests() {
  const [items, setItems] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isActive = true;

    async function load() {
      try {
        // Fetch incoming request ids.
        const { ids } = await fetchConnectionRequests();
        // Fetch user summary data for each id.
        const summaries = await Promise.all(ids.map(fetchUserSummary));
        if (isActive) {
          setItems(summaries);
        }
      } catch (err) {
        if (isActive) {
          setError(err instanceof Error ? err.message : "Failed to load");
        }
      } finally {
        if (isActive) {
          setLoading(false);
        }
      }
    }

    load();

    return () => {
      isActive = false;
    };
  }, []);

  async function handleAccept(userId: number) {
    try {
      // Accept the incoming request.
      await acceptConnectionRequest(userId);
      // Remove the request locally after success.
      setItems((prev) => prev.filter((item) => item.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to accept");
    }
  }

  async function handleDismiss(userId: number) {
    try {
      // Dismiss the incoming request.
      await dismissConnectionRequest(userId);
      // Remove the request locally after success.
      setItems((prev) => prev.filter((item) => item.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to dismiss");
    }
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Connection Requests</h1>
        <p>People who want to connect with you.</p>
      </div>

      {loading && <p className="muted">Loading requests…</p>}
      {error && <p className="muted">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No requests right now.</p>
      )}

      <div className="card-stack">
        {items.map((item) => (
          <article className="request-card" key={item.id}>
            <div className="avatar">
              {item.name?.slice(0, 2).toUpperCase() ?? "MM"}
            </div>
            <div className="profile-meta">
              <h3>{item.name ?? `User ${item.id}`}</h3>
              <p>Shared interests pending · Review profile</p>
            </div>
            <div className="profile-actions">
              <button
                className="primary-button"
                type="button"
                onClick={() => handleAccept(item.id)}
              >
                Accept
              </button>
              <button
                className="ghost-button"
                type="button"
                onClick={() => handleDismiss(item.id)}
              >
                Dismiss
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
