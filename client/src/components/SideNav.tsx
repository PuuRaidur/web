import { NavLink } from "react-router-dom";

const links = [
  { to: "/recommendations", label: "Recommendations" },
  { to: "/pending", label: "Pending" },
  { to: "/requests", label: "Requests" },
  { to: "/connections", label: "Connections" },
];

export default function SideNav() {
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
            {link.label}
          </NavLink>
        ))}
      </nav>
      <div className="nav-footer">
        <button className="ghost-button" type="button">
          Log out
        </button>
      </div>
    </aside>
  );
}
