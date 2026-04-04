import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Recommendations from "./pages/Recommendations";
import Requests from "./pages/Requests";
import Connections from "./pages/Connections";
import Pending from "./pages/Pending";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ProfileSetup from "./pages/ProfileSetup";
import Chats from "./pages/Chats";
import "./App.css";

export default function App() {
  // Simple auth check based on stored token.
  const isAuthed = Boolean(localStorage.getItem("auth_token"));

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route element={<AppLayout />}>
        <Route
          path="/"
          element={
            isAuthed ? (
              <Navigate to="/recommendations" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />
        <Route
          path="/recommendations"
          element={isAuthed ? <Recommendations /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/pending"
          element={isAuthed ? <Pending /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/profile"
          element={isAuthed ? <ProfileSetup /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/chats"
          element={isAuthed ? <Chats /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/requests"
          element={isAuthed ? <Requests /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/connections"
          element={isAuthed ? <Connections /> : <Navigate to="/login" replace />}
        />
        <Route path="*" element={<Navigate to="/recommendations" replace />} />
      </Route>
    </Routes>
  );
}
