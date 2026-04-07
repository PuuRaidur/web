import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "./AuthContext";
import App from "./App.tsx";
import "./index.css";

// Polyfill for libraries expecting a Node-style global.
if (typeof window !== "undefined" && !(window as unknown as { global?: Window }).global) {
  (window as unknown as { global: Window }).global = window;
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>
);
