import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { WebSocketProvider } from './context/WebSocketContext';
import { GuestLandingPage } from './pages/GuestLandingPage';
import { GamePage } from './pages/GamePage';
import { RegistrationPage } from './pages/RegistrationPage';
import { LoginPage } from './pages/LoginPage';
import { ProfilePage } from './pages/ProfilePage';
import { ComputerGamePage } from './pages/ComputerGamePage';
import { PrivateGamePage } from './pages/PrivateGamePage';
import RankedGamePage from './pages/RankedGamePage';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { TestBoard } from './components/game/TestBoard';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <WebSocketProvider>
        <Router>
          <Routes>
            <Route path="/" element={<GuestLandingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegistrationPage />} />
            <Route 
              path="/profile" 
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              } 
            />
            <Route path="/computer" element={<ComputerGamePage />} />
            <Route path="/private" element={<PrivateGamePage />} />
            <Route path="/private/:code" element={<PrivateGamePage />} />
            <Route 
              path="/ranked" 
              element={
                <ProtectedRoute>
                  <RankedGamePage />
                </ProtectedRoute>
              } 
            />
            <Route path="/game/:id" element={<GamePage />} />
            <Route path="/test" element={<TestBoard />} />
          </Routes>
        </Router>
      </WebSocketProvider>
    </AuthProvider>
  );
}

export default App;
