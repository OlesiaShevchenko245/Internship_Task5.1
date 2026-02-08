import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "./AuthProvider";

export default function RequireAuth({ children }) {
  const { loading, profile } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div style={{ padding: 24 }}>Loading session…</div>;
  }

  if (!profile) {
    const from = `${location.pathname}${location.search || ""}`;
    return <Navigate to="/login" replace state={{ from }} />;
  }

  return children;
}
