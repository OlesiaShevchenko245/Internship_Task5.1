import { useEffect, useMemo, useState } from "react";
import { useSearchParams, useNavigate, useLocation } from "react-router-dom";
import { listObservations, deleteObservation } from "../services/observationApi";
import { getAuthors } from "../services/authorApi";
import { useAuth } from "../pageProviders/AuthProvider";
import "./ObservationsListPage.css";

function ObservationsListPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();

  const { profile, logout } = useAuth();

  const page = Number(searchParams.get("page") ?? 1); 
  const size = Number(searchParams.get("size") ?? 10);

  const spAuthorId = searchParams.get("authorId") || "";
  const spName = searchParams.get("name") || "";
  const spStartTime = searchParams.get("startTime") || "";

  const [filterAuthorId, setFilterAuthorId] = useState(spAuthorId);
  const [filterName, setFilterName] = useState(spName);
  const [filterStartTime, setFilterStartTime] = useState(spStartTime);

  const [observations, setObservations] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(false);

  const [hoveredId, setHoveredId] = useState(null);
  const [observationToDelete, setObservationToDelete] = useState(null);
  const [deleteError, setDeleteError] = useState(null);
  const [toastMessage, setToastMessage] = useState(null);

  useEffect(() => {
    getAuthors().then(setAuthors).catch(() => {
      setToastMessage("Failed to load authors");
      setTimeout(() => setToastMessage(null), 3000);
    });
  }, []);

  useEffect(() => {
    setFilterAuthorId(spAuthorId);
    setFilterName(spName);
    setFilterStartTime(spStartTime);
  }, [spAuthorId, spName, spStartTime]);

  const requestBody = useMemo(() => {
    const authorId = spAuthorId ? Number(spAuthorId) : undefined;

    const name = spName?.trim() ? spName.trim() : undefined;

    const startTime = spStartTime ? `${spStartTime}:00` : undefined;

    return {
      page,
      size,
      ...(authorId ? { authorId } : {}),
      ...(name ? { name } : {}),
      ...(startTime ? { startTime } : {}),
    };
  }, [page, size, spAuthorId, spName, spStartTime]);

  const loadObservations = () => {
    setLoading(true);

    listObservations(requestBody)
      .then((res) => {
        setObservations(res.list || []);
        setTotalPages(res.totalPages || 0);
      })
      .catch(() => {
        setToastMessage("Failed to load observations");
        setTimeout(() => setToastMessage(null), 3000);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadObservations();
  }, [requestBody]);

  const applyFilters = () => {
    const params = {};

    if (filterAuthorId) params.authorId = filterAuthorId;
    if (filterName?.trim()) params.name = filterName.trim();
    if (filterStartTime) params.startTime = filterStartTime;

    params.page = 1;
    params.size = String(size);

    setSearchParams(params);
  };

  const clearFilters = () => {
    setFilterAuthorId("");
    setFilterName("");
    setFilterStartTime("");
    setSearchParams({ page: "1", size: String(size) });
  };

  const goToPage = (newPage) => {
    const params = Object.fromEntries(searchParams.entries());
    params.page = String(newPage);
    setSearchParams(params);
  };

  const handleDelete = async () => {
    try {
      setDeleteError(null);
      await deleteObservation(observationToDelete.id);
      setObservationToDelete(null);
      setToastMessage("Observation deleted successfully");
      setTimeout(() => setToastMessage(null), 3000);
      loadObservations();
    } catch {
      setDeleteError("An error occurred while deleting");
    }
  };

  return (
    <div className="container">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 12 }}>
        <div>
          <h1 className="title" style={{ marginBottom: 6 }}>Observations list</h1>
          <div style={{ fontSize: 13, opacity: 0.8 }}>
            Logged in as <b>{profile?.name || "User"}</b> ({profile?.email || "—"})
          </div>
        </div>

        <button className="primaryButton" onClick={logout}>Logout</button>
      </div>

      <div className="filterBox">
        <h3>Filters</h3>
        <div className="filterRow">
          <div>
            <label>Author (observation author)</label>
            <select value={filterAuthorId} onChange={(e) => setFilterAuthorId(e.target.value)}>
              <option value="">All</option>
              {authors.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.firstName} {a.lastName}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label>Name</label>
            <input value={filterName} onChange={(e) => setFilterName(e.target.value)} />
          </div>

          <div>
            <label>Start time</label>
            <input
              type="datetime-local"
              value={filterStartTime}
              onChange={(e) => setFilterStartTime(e.target.value)}
            />
          </div>

          <button className="primaryButton" onClick={applyFilters}>
            Apply
          </button>
          <button onClick={clearFilters}>
            Clear
          </button>
        </div>
      </div>

      <button className="primaryButton" onClick={() => navigate(`/observations/new${location.search}`)}>
        Add new entity
      </button>

      {loading ? (
        <p>Loading...</p>
      ) : observations.length === 0 ? (
        <p>The list is empty</p>
      ) : (
        <ul className="list">
          {observations.map((obs) => (
            <li
              key={obs.id}
              className="listItem"
              onClick={() => navigate(`/observations/${obs.id}${location.search}`)}
              onMouseEnter={() => setHoveredId(obs.id)}
              onMouseLeave={() => setHoveredId(null)}
            >
              {hoveredId === obs.id && (
                <button
                  className="deleteButton"
                  onClick={(e) => {
                    e.stopPropagation();
                    setObservationToDelete(obs);
                  }}
                  title="Delete"
                >
                  🗑
                </button>
              )}

              <h3>{obs.name}</h3>
              <div className="meta">
                <div>
                  <strong>Date:</strong> {new Date(obs.observationTime).toLocaleString()}
                </div>
                <div>
                  <strong>Author:</strong> {obs.authorName}
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={page === 1} onClick={() => goToPage(page - 1)}>
            Previous
          </button>
          <span>
            Page {page} of {totalPages}
          </span>
          <button disabled={page >= totalPages} onClick={() => goToPage(page + 1)}>
            Next
          </button>
        </div>
      )}

      {observationToDelete && (
        <div className="modalOverlay">
          <div className="filterBox">
            <h3>Confirmation</h3>
            <p>
              Are you sure you want to delete <strong>{observationToDelete.name}</strong>?
            </p>
            {deleteError && <p className="errorText">{deleteError}</p>}
            <div className="actions">
              <button onClick={() => setObservationToDelete(null)}>Cancel</button>
              <button className="primaryButton" onClick={handleDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}

      {toastMessage && <div className="toast">{toastMessage}</div>}
    </div>
  );
}

export default ObservationsListPage;
