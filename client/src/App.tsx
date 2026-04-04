import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Recommendations from "./pages/Recommendations";
import Requests from "./pages/Requests";
import Connections from "./pages/Connections";
import Pending from "./pages/Pending";
import "./App.css";

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<Navigate to="/recommendations" replace />} />
        <Route path="/recommendations" element={<Recommendations />} />
        <Route path="/pending" element={<Pending />} />
        <Route path="/requests" element={<Requests />} />
        <Route path="/connections" element={<Connections />} />
        <Route path="*" element={<Navigate to="/recommendations" replace />} />
      </Route>
    </Routes>
  );
}
