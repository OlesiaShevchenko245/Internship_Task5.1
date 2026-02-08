import { apiFetch, apiJson } from "./apiClient";

export async function listObservations(filterRequest) {
  return apiJson("/api/observation/_list", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(filterRequest),
  });
}

export async function getObservationById(id) {
  return apiJson(`/api/observation/${id}`, { method: "GET" });
}

export async function createObservation(data) {
  return apiJson("/api/observation", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export async function updateObservation(id, data) {
  return apiJson(`/api/observation/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export async function deleteObservation(id) {
  const res = await apiFetch(`/api/observation/${id}`, { method: "DELETE" });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || "Failed to delete observation");
  }
}
