import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchConnections,
  fetchUserSummary,
  getOrCreateChat,
  disconnect,
} from "../api/client";
import type { UserSummary } from "../api/types";
import Avatar from "../components/Avatar";

type ConnectionItem = {
  connectionId: number;
  user: UserSummary;
};

export default function Connections() {
  const navigate = useNavigate();
  const [items, setItems] = useState<ConnectionItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actingOn, setActingOn] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const details = await fetchConnections();
      if (details.length === 0) {
        setItems([]);
        return;
      }
      const summaries = await Promise.all(
        details.map((d) => fetchUserSummary(d.otherUserId))
      );
      const merged: ConnectionItem[] = details.map((d, i) => ({
        connectionId: d.connectionId,
        user: summaries[i],
      }));
      setItems(merged);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function handleMessage(userId: number) {
    try {
      const { chatId } = await getOrCreateChat(userId);
      navigate(`/chats?chatId=${chatId}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to open chat");
    }
  }

  async function handleDisconnect(connectionId: number) {
    setActingOn(connectionId);
    try {
      await disconnect(connectionId);
      setItems((prev) => prev.filter((c) => c.connectionId !== connectionId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to disconnect");
    } finally {
      setActingOn(null);
    }
  }

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Your Network</p>
          <h1>Connections</h1>
          <p className="subtitle">
            Keep track of everyone you have matched with.
          </p>
        </div>
      </header>

      {loading && <p className="muted">Loading connections…</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && items.length === 0 && (
        <p className="muted">No connections yet.</p>
      )}

      <ul className="user-list">
        {items.map((item) => (
          <li className="user-list-item" key={item.connectionId}>
            <Avatar name={item.user.name} url={item.user.profilePictureUrl} />
            <div className="user-info">
              <h3>{item.user.name ?? `User ${item.user.id}`}</h3>
            </div>
            <div className="user-actions">
              <button
                className="ghost-button"
                type="button"
                onClick={() => handleMessage(item.user.id)}
              >
                Message
              </button>
              <button
                className="danger-button"
                type="button"
                disabled={actingOn !== null}
                onClick={() => handleDisconnect(item.connectionId)}
              >
                {actingOn === item.connectionId
                  ? "Disconnecting…"
                  : "Disconnect"}
              </button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}
