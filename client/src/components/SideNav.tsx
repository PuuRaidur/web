import { NavLink, useNavigate } from "react-router-dom";

const links = [
  { to: "/recommendations", label: "Recommendations" },
  { to: "/profile", label: "Profile" },
  { to: "/chats", label: "Chats" },
  { to: "/pending", label: "Pending" },
  { to: "/requests", label: "Requests" },
  { to: "/connections", label: "Connections" },
];

type SideNavProps = {
  unreadCount?: number;
};

export default function SideNav({ unreadCount = 0 }: SideNavProps) {
  const navigate = useNavigate();

  function handleLogout() {
    // Remove stored auth token and redirect to login.
    localStorage.removeItem("auth_token");
    localStorage.removeItem("auth_email");
    navigate("/login");
  }

  return (
    <aside className="side-nav">
      <div className="brand">
        <div className="brand-mark">MM</div>
        <div className="brand-text">
          <div className="brand-title">Match-Me</div>
          <div className="brand-sub">Find your people</div>
        </div>
      </div>
      <nav className="nav-links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `nav-link ${isActive ? "nav-link-active" : ""}`
            }
          >
            <span>{link.label}</span>
            {link.to === "/chats" && unreadCount > 0 && (
              <span className="nav-badge">{unreadCount}</span>
            )}
          </NavLink>
        ))}
      </nav>
      <div className="nav-footer">
        <button className="ghost-button" type="button" onClick={handleLogout}>
          Log out
        </button>
      </div>
    </aside>
  );
}
