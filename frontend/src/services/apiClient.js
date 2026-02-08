export async function apiFetch(path, options = {}) {
  return fetch(path, {
    credentials: "include",
    ...options,
    headers: { ...(options.headers || {}) },
  });
}

export async function apiJson(path, options = {}) {
  const res = await apiFetch(path, options);
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    const err = new Error(text || `HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }
  return res.json();
}
