import { Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useState } from "react";
import AppLayout from "./layouts/AppLayout";
import Recommendations from "./pages/Recommendations";
import Requests from "./pages/Requests";
import Connections from "./pages/Connections";
import Pending from "./pages/Pending";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ProfileSetup from "./pages/ProfileSetup";
import Chats from "./pages/Chats";
import { fetchMyBio, fetchMyProfile } from "./api/client";
import "./App.css";

export default function App() {
  // Simple auth check based on stored token.
  const isAuthed = Boolean(localStorage.getItem("auth_token"));
  const [profileReady, setProfileReady] = useState<boolean | null>(null);

  useEffect(() => {
    let isActive = true;

    async function checkProfile() {
      if (!isAuthed) {
        setProfileReady(false);
        return;
      }

      try {
        const profile = await fetchMyProfile();
        const bio = await fetchMyBio();
        const complete =
          Boolean(profile?.displayName) &&
          Boolean(profile?.location) &&
          Boolean(bio?.hobbies) &&
          Boolean(bio?.musicPreferences) &&
          Boolean(bio?.foodPreferences) &&
          Boolean(bio?.interests) &&
          Boolean(bio?.lookingFor);
        if (isActive) {
          setProfileReady(complete);
        }
      } catch {
        if (isActive) {
          setProfileReady(false);
        }
      }
    }

    checkProfile();

    return () => {
      isActive = false;
    };
  }, [isAuthed]);

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route element={<AppLayout />}>
        <Route
          path="/"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Navigate to="/recommendations" replace />
            )
          }
        />
        <Route
          path="/recommendations"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Recommendations />
            )
          }
        />
        <Route
          path="/pending"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Pending />
            )
          }
        />
        <Route
          path="/profile"
          element={isAuthed ? <ProfileSetup /> : <Navigate to="/login" replace />}
        />
        <Route
          path="/chats"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Chats />
            )
          }
        />
        <Route
          path="/requests"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Requests />
            )
          }
        />
        <Route
          path="/connections"
          element={
            !isAuthed ? (
              <Navigate to="/login" replace />
            ) : profileReady === false ? (
              <Navigate to="/profile" replace />
            ) : (
              <Connections />
            )
          }
        />
        <Route path="*" element={<Navigate to="/recommendations" replace />} />
      </Route>
    </Routes>
  );
}
