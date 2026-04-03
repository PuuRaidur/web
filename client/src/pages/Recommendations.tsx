export default function Recommendations() {
  return (
    <section className="page">
      <div className="page-head">
        <h1>Recommendations</h1>
        <p>Top matches based on your bio and preferences.</p>
      </div>
      <div className="card-grid">
        {Array.from({ length: 6 }).map((_, index) => (
          <article className="profile-card" key={index}>
            <div className="avatar">MM</div>
            <div className="profile-meta">
              <h3>User {index + 1}</h3>
              <p>Music: Jazz · Food: Sushi · Interests: Tech</p>
            </div>
            <div className="profile-actions">
              <button className="primary-button" type="button">
                Connect
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
