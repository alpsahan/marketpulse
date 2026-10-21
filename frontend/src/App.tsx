import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import AlertsPage from "./pages/AlertsPage";
import ProtectedRoute from "./components/ProtectedRoute";
import RegisterPage from "./pages/RegisterPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/alerts"
          element={
            <ProtectedRoute>
              <AlertsPage />
            </ProtectedRoute>
          }
        />

        <Route path="/" element={<Navigate to="/login" />} />
        <Route
          path="/register"
          element={<RegisterPage />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;