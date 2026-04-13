import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../api/client";

export default function Register() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Call backend to create account and get JWT.
      const { token } = await register(email, password);
      // Store token for future API calls.
      localStorage.setItem("auth_token", token);
      localStorage.setItem("auth_email", email);
      // Redirect to main app.
      navigate("/recommendations");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-shell">
      <div className="auth-card">
        <div className="auth-header">
          <div className="brand-mark">MM</div>
          <div>
            <h1>Create account</h1>
            <p className="muted">Join Match-Me in one step.</p>
          </div>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label className="auth-field">
            <span>Email</span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="you@example.com"
              required
            />
          </label>
          <label className="auth-field">
            <span>Password</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Choose a secure password"
              required
            />
          </label>
          {error && <p className="auth-error">{error}</p>}
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? "Creating…" : "Create account"}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </section>
  );
}
