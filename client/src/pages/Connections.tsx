export default function Connections() {
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

      <div className="card-grid">
        <article className="connection-card">
          <div className="avatar">AL</div>
          <div>
            <h3>Alex Li</h3>
            <p className="muted">Tallinn · Product · Board games</p>
          </div>
          <div className="connection-actions">
            <button className="ghost-button" type="button">
              Message
            </button>
            <button className="danger-button" type="button">
              Disconnect
            </button>
          </div>
        </article>
      </div>
    </section>
  );
}
