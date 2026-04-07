import { useEffect, useState, useCallback } from "react";
import {
  getIncomingRequests,
  getUserSummaries,
  acceptConnectionRequest,
  dismissConnectionRequest,
  type ConnectionRequestDetail,
  type UserSummary,
} from "../api";

type RequestWithUser = ConnectionRequestDetail & { user: UserSummary };

export default function Requests() {
  const [requests, setRequests] = useState<RequestWithUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [acting, setActing] = useState<number | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const incoming = await getIncomingRequests();
      if (incoming.length === 0) {
        setRequests([]);
        return;
      }
      const summaries = await getUserSummaries(incoming.map((r) => r.senderId));
      const merged = incoming.map((req) => ({
        ...req,
        user: summaries.find((s) => s.id === req.senderId)!,
      }));
      setRequests(merged);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to load requests";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleAccept = async (requestId: number) => {
    setActing(requestId);
    try {
      await acceptConnectionRequest(requestId);
      setRequests((prev) => prev.filter((r) => r.id !== requestId));
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to accept request";
      setError(msg);
    } finally {
      setActing(null);
    }
  };

  const handleDismiss = async (requestId: number) => {
    setActing(requestId);
    try {
      await dismissConnectionRequest(requestId);
      setRequests((prev) => prev.filter((r) => r.id !== requestId));
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : "Failed to dismiss request";
      setError(msg);
    } finally {
      setActing(null);
    }
  };

  if (loading) return <p>Loading connection requests…</p>;
  if (error) return <p className="error-text">{error}</p>;
  if (requests.length === 0) return <p>No pending connection requests.</p>;

  return (
    <section className="page">
      <div className="page-head">
        <div>
          <h1>Connection Requests</h1>
          <p className="subtitle">People who want to connect with you.</p>
        </div>
      </div>

      <ul className="user-list">
        {requests.map((req) => (
          <li className="user-list-item" key={req.id}>
            <div className="avatar">
              {req.user.profilePictureUrl ? (
                <img src={req.user.profilePictureUrl} alt={req.user.name || "User"} />
              ) : (
                initials(req.user.name)
              )}
            </div>
            <div className="user-info">
              <h3>{req.user.name || `User ${req.user.id}`}</h3>
            </div>
            <div className="user-actions">
              <button
                className="primary-button"
                type="button"
                disabled={acting !== null}
                onClick={() => handleAccept(req.id)}
              >
                {acting === req.id ? "Accepting…" : "Accept"}
              </button>
              <button
                className="ghost-button"
                type="button"
                disabled={acting !== null}
                onClick={() => handleDismiss(req.id)}
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
