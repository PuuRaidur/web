export default function Requests() {
  return (
    <section className="page">
      <div className="page-head">
        <h1>Connection Requests</h1>
        <p>People who want to connect with you.</p>
      </div>
      <div className="card-stack">
        {Array.from({ length: 3 }).map((_, index) => (
          <article className="request-card" key={index}>
            <div className="avatar">U{index + 1}</div>
            <div className="profile-meta">
              <h3>Requester {index + 1}</h3>
              <p>Shared interests: Travel · Music · Coffee</p>
            </div>
            <div className="profile-actions">
              <button className="primary-button" type="button">
                Accept
              </button>
              <button className="ghost-button" type="button">
                Dismiss
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
