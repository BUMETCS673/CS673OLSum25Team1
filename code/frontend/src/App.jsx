import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Login from "./pages/Login";
import Register from "./pages/Register";
import RegisterConfirm from "./pages/RegisterConfirm";
import Home from "./pages/Home";
import ActivityDiscovery from "./pages/ActivityDiscovery";
import CreateActivityPage from "./pages/CreateActivityPage";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register/confirm" element={<RegisterConfirm />} />
          <Route path="/register" element={<Register />} />

          <Route
            path="/home"
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
          <Route
            path="/activity-discovery"
            element={
              <ProtectedRoute>
                <ActivityDiscovery />
              </ProtectedRoute>
            }
          />
          <Route
            path="/create-activity"
            element={
              <ProtectedRoute>
                <CreateActivityPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
