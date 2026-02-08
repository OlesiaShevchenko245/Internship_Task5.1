import React, { useEffect, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { loginWithGoogle } from "../../services/authApi";
import { useAuth } from "../../pageProviders/AuthProvider";

export default function LoginPage() {
  const { loading, profile, refreshProfile } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const from = useMemo(
    () => location.state?.from || "/observations",
    [location.state]
  );

  useEffect(() => {
    refreshProfile();
  }, [refreshProfile]);

  useEffect(() => {
    if (!loading && profile) {
      navigate(from, { replace: true });
    }
  }, [loading, profile, from, navigate]);

  return (
    <div style={{ padding: 24, maxWidth: 520 }}>
      <h2>Login required</h2>
      <p>To use the website, login with Google.</p>

      <button onClick={loginWithGoogle} style={{ padding: "10px 14px" }}>
        Login with Google
      </button>

      <div style={{ marginTop: 12, opacity: 0.7, fontSize: 14 }}>
        After login you will be returned to the previous page: <b>{from}</b>
      </div>
    </div>
  );
}
