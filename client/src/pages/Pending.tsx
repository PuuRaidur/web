import { useEffect, useState } from "react";
import { fetchOutgoingConnectionRequests, fetchUserSummary } from "../api/client";
import type { UserSummary } from "../api/types";

export default function Pending() {
  const [items, setItems] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isActive = true;

    async function load() {
      try {
        // Fetch outgoing request ids.
        const { ids } = await fetchOutgoingConnectionRequests();
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

  return (
    <section className="page">
      <div className="page-head">
        <h1>Pending Requests</h1>
        <p>People you have requested to connect with.</p>
      </div>

      {loading && <p className="muted">Loading pending requests…</p>}
      {error && <p className="muted">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No pending requests right now.</p>
      )}

      <div className="card-stack">
        {items.map((item) => (
          <article className="request-card" key={item.id}>
            <div className="avatar">
              {item.name?.slice(0, 2).toUpperCase() ?? "MM"}
            </div>
            <div className="profile-meta">
              <h3>{item.name ?? `User ${item.id}`}</h3>
              <p>Awaiting response</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
