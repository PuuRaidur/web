import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import { login as apiLogin, register as apiRegister, fetchMe } from "./api/client";

interface AuthContextValue {
  token: string | null;
  userId: number | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}

async function resolveUserId(token: string): Promise<number> {
  const prev = localStorage.getItem("auth_token");
  localStorage.setItem("auth_token", token);
  try {
    const me = await fetchMe();
    return me.id;
  } finally {
    if (prev === null && token !== prev) {
      // token was newly set, keep it
    }
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("auth_token"));
  const [userId, setUserId] = useState<number | null>(() => {
    const stored = localStorage.getItem("user_id");
    return stored ? Number(stored) : null;
  });

  const login = useCallback(async (email: string, password: string) => {
    const res = await apiLogin(email, password);
    setToken(res.token);
    localStorage.setItem("auth_token", res.token);
    const id = await resolveUserId(res.token);
    setUserId(id);
    localStorage.setItem("user_id", String(id));
  }, []);

  const register = useCallback(async (email: string, password: string) => {
    const res = await apiRegister(email, password);
    setToken(res.token);
    localStorage.setItem("auth_token", res.token);
    const id = await resolveUserId(res.token);
    setUserId(id);
    localStorage.setItem("user_id", String(id));
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    setUserId(null);
    localStorage.removeItem("auth_token");
    localStorage.removeItem("user_id");
  }, []);

  return (
    <AuthContext.Provider
      value={{
        token,
        userId,
        isAuthenticated: token !== null,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
