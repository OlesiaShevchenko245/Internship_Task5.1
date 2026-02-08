import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import ObservationsListPage from "./pages/ObservationsListPage";
import ObservationDetailsPage from "./pages/ObservationDetailsPage";

import { AuthProvider } from "./pageProviders/AuthProvider";
import RequireAuth from "./pageProviders/RequireAuth";
import LoginPage from "./pages/login/LoginPage";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/observations" />} />

          <Route path="/login" element={<LoginPage />} />

          <Route
            path="/observations"
            element={
              <RequireAuth>
                <ObservationsListPage />
              </RequireAuth>
            }
          />
          <Route
            path="/observations/:id"
            element={
              <RequireAuth>
                <ObservationDetailsPage />
              </RequireAuth>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
