import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchConnections,
  fetchUserSummary,
  getOrCreateChat,
  disconnectWithUser,
} from "../api/client";
import type { UserSummary } from "../api/types";
import Avatar from "../components/Avatar";

export default function Connections() {
  const navigate = useNavigate();
  const [items, setItems] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isActive = true;

    async function load() {
      try {
        // Fetch connection ids.
        const { ids } = await fetchConnections();
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

  async function handleMessage(userId: number) {
    try {
      // Ensure the chat exists before navigating.
      const { chatId } = await getOrCreateChat(userId);
      navigate(`/chats?chatId=${chatId}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to open chat");
    }
  }

  async function handleDisconnect(userId: number) {
    try {
      await disconnectWithUser(userId);
      setItems((prev) => prev.filter((item) => item.id !== userId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to disconnect");
    }
  }

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Your Network</p>
          <h1>Connections</h1>
          <p className="subtitle">
            Keep track of everyone you have matched with and keep the
            conversation going.
          </p>
        </div>
        <button className="primary-button" type="button">
          New chat
        </button>
      </header>

      {loading && <p className="muted">Loading connections…</p>}
      {error && <p className="muted">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No connections yet.</p>
      )}

      <div className="card-grid">
        {items.map((item) => (
          <article className="connection-card" key={item.id}>
            <Avatar name={item.name} url={item.profilePictureUrl} />
            <div>
              <h3>{item.name ?? `User ${item.id}`}</h3>
              <p className="muted">Ready to chat</p>
            </div>
            <div className="connection-actions">
              <button
                className="ghost-button"
                type="button"
                onClick={() => handleMessage(item.id)}
              >
                Message
              </button>
              <button
                className="danger-button"
                type="button"
                onClick={() => handleDisconnect(item.id)}
              >
                Disconnect
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
