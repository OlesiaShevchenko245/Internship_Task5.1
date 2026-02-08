import { apiFetch } from "./apiClient";

export async function fetchProfile() {
  const res = await apiFetch("/profile", { method: "GET" });

  if (res.status === 401) return { ok: false, status: 401, profile: null };
  if (!res.ok) return { ok: false, status: res.status, profile: null };

  const data = await res.json();
  return { ok: true, status: 200, profile: data };
}

export function loginWithGoogle() {
  window.location.href = `/oauth2/authorization/google`;
}

export function logout() {
  window.location.href = `/logout`;
}
