import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { fetchProfile, logout as logoutRedirect } from "../services/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState(null);

  const inFlightRef = useRef(null);

  const refreshProfile = useCallback(async () => {
    if (inFlightRef.current) return inFlightRef.current;

    const p = (async () => {
      setLoading(true);
      try {
        const res = await fetchProfile();
        setProfile(res.ok ? res.profile : null);
        return res.ok;
      } catch {
        setProfile(null);
        return false;
      } finally {
        setLoading(false);
        inFlightRef.current = null;
      }
    })();

    inFlightRef.current = p;
    return p;
  }, []);

  const logoutLocal = useCallback(() => {
    setProfile(null);
  }, []);

  const logout = useCallback(() => {
    logoutLocal();
    logoutRedirect();
  }, [logoutLocal]);

  useEffect(() => {
    refreshProfile();
  }, [refreshProfile]);

  useEffect(() => {
    const onVisibility = () => {
      if (document.visibilityState === "visible") {
        refreshProfile();
      }
    };
    document.addEventListener("visibilitychange", onVisibility);
    return () => document.removeEventListener("visibilitychange", onVisibility);
  }, [refreshProfile]);

  const value = useMemo(
    () => ({ loading, profile, refreshProfile, logoutLocal, logout }),
    [loading, profile, refreshProfile, logoutLocal, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
