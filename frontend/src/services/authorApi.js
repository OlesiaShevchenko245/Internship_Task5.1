import { apiJson } from "./apiClient";

export async function getAuthors() {
  return apiJson("/api/author", { method: "GET" });
}
