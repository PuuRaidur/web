import { useEffect, useState } from "react";
import {
  fetchMe,
  fetchOutgoingConnectionRequests,
  fetchRecommendations,
  fetchUserSummary,
  sendConnectionRequest,
} from "../api/client";
import type { UserSummary } from "../api/types";

export default function Recommendations() {
  const [items, setItems] = useState<UserSummary[]>([]);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isActive = true;

    async function load() {
      try {
        setLoading(true);
        // Load the current user to avoid self-requests.
        const me = await fetchMe();
        // Load outgoing requests so we do not recommend them again.
        const outgoing = await fetchOutgoingConnectionRequests();
        // Fetch recommendation ids first.
        const { ids } = await fetchRecommendations();
        // Filter out the current user if the backend still includes it.
        const filteredIds = ids.filter(
          (id) => id !== me.id && !outgoing.ids.includes(id)
        );
        // Fetch user summary data for each id.
        const summaries = await Promise.all(filteredIds.map(fetchUserSummary));
        if (isActive) {
          setItems(summaries);
          setCurrentUserId(me.id);
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

  async function handleConnect(userId: number) {
    if (currentUserId && userId === currentUserId) {
      setError("You cannot connect to yourself.");
      return;
    }

    try {
      // Send a connection request to the user.
      await sendConnectionRequest(userId);
      // Refresh recommendations after success.
      const me = await fetchMe();
      const outgoing = await fetchOutgoingConnectionRequests();
      const { ids } = await fetchRecommendations();
      const filteredIds = ids.filter(
        (id) => id !== me.id && !outgoing.ids.includes(id)
      );
      const summaries = await Promise.all(filteredIds.map(fetchUserSummary));
      setItems(summaries);
      setCurrentUserId(me.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to connect");
    }
  }

  function handleDismiss(userId: number) {
    // For now we only hide it on the UI.
    setItems((prev) => prev.filter((item) => item.id !== userId));
  }

  return (
    <section className="page">
      <div className="page-head">
        <h1>Recommendations</h1>
        <p>Top matches based on your bio and preferences.</p>
      </div>

      {loading && <p className="muted">Loading recommendations…</p>}
      {error && <p className="muted">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No recommendations yet.</p>
      )}

      <div className="card-grid">
        {items.map((item) => (
          <article className="profile-card" key={item.id}>
            <div className="avatar">
              {item.name?.slice(0, 2).toUpperCase() ?? "MM"}
            </div>
            <div className="profile-meta">
              <h3>{item.name ?? `User ${item.id}`}</h3>
              <p>Open to connect · Ask for details</p>
            </div>
            <div className="profile-actions">
              <button
                className="primary-button"
                type="button"
                onClick={() => handleConnect(item.id)}
              >
                Connect
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
