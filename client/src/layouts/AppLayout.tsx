import { Outlet } from "react-router-dom";
import SideNav from "../components/SideNav";

export default function AppLayout() {
  return (
    <div className="app-shell">
      <SideNav />
      <main className="app-main">
        <div className="page-wrapper">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
