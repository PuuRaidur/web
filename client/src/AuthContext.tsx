import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import { login as apiLogin, register as apiRegister, type AuthResponse } from "./api";

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

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));
  const [userId, setUserId] = useState<number | null>(() => {
    const stored = localStorage.getItem("userId");
    return stored ? Number(stored) : null;
  });

  const login = useCallback(async (email: string, password: string) => {
    const res: AuthResponse = await apiLogin({ email, password });
    setToken(res.token);
    setUserId(res.userId);
    localStorage.setItem("token", res.token);
    localStorage.setItem("userId", String(res.userId));
  }, []);

  const register = useCallback(async (email: string, password: string) => {
    const res: AuthResponse = await apiRegister({ email, password });
    setToken(res.token);
    setUserId(res.userId);
    localStorage.setItem("token", res.token);
    localStorage.setItem("userId", String(res.userId));
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    setUserId(null);
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
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
