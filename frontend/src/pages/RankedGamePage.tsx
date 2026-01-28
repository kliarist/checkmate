import { useState } from 'react';
import TimeControlSelector from '../components/matchmaking/TimeControlSelector';
import MatchmakingModal from '../components/matchmaking/MatchmakingModal';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { menuStyles } from '../styles/menuStyles';

/**
 * RankedGamePage - Entry point for ranked matchmaking
 * Allows users to select time control and join matchmaking queue
 */
const RankedGamePage = () => {
  const [isSearching, setIsSearching] = useState(false);
  const [selectedTimeControl, setSelectedTimeControl] = useState<string | null>(null);
  const { user } = useAuth();
  const navigate = useNavigate();

  // Check if user is logged in
  const isLoggedIn = !!localStorage.getItem('token');
  const userName = localStorage.getItem('username');
  const userEmail = localStorage.getItem('userEmail');

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  const handleTimeControlSelect = (timeControl: string) => {
    setSelectedTimeControl(timeControl);
    setIsSearching(true);
  };

  const handleCancelSearch = () => {
    setIsSearching(false);
    setSelectedTimeControl(null);
  };

  const handleGameFound = (gameId: string) => {
    setIsSearching(false);
    navigate(`/game/${gameId}`);
  };

  if (!user) {
    return (
      <div style={menuStyles.container}>
        {/* User info bar at top if logged in */}
        {isLoggedIn && (
          <div style={{
            position: 'absolute',
            top: 0,
            right: 0,
            padding: '1rem 2rem',
            display: 'flex',
            alignItems: 'center',
            gap: '1rem',
          }}>
            <span style={{ color: '#e0e0e0' }}>
              {userName || userEmail}
            </span>
            <button
              onClick={() => navigate('/profile')}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#3a3a3a',
                color: '#e0e0e0',
                border: '1px solid #4a4a4a',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.9rem',
              }}
            >
              Profile
            </button>
            <button
              onClick={handleLogout}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: 'transparent',
                color: '#999',
                border: '1px solid #3a3a3a',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.9rem',
              }}
            >
              Logout
            </button>
          </div>
        )}

        <button onClick={() => navigate('/')} style={menuStyles.backButton}>
          ← Back
        </button>
        <div style={{ ...menuStyles.card, maxWidth: '400px' }}>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'center',
            marginBottom: '1rem' 
          }}>
            <svg width="80" height="80" viewBox="0 0 45 45" xmlns="http://www.w3.org/2000/svg">
              <g fill="none" fillRule="evenodd" stroke="#b58863" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M 9,39 L 36,39 L 36,36 L 9,36 L 9,39 z" fill="#b58863"/>
                <path d="M 12,36 L 12,32 L 33,32 L 33,36 L 12,36 z" fill="#b58863"/>
                <path d="M 11,14 L 11,9 L 15,9 L 15,11 L 20,11 L 20,9 L 25,9 L 25,11 L 30,11 L 30,9 L 34,9 L 34,14" fill="#b58863"/>
                <path d="M 34,14 L 31,17 L 31,29.5 L 33,32 L 12,32 L 14,29.5 L 14,17 L 11,14" fill="#b58863"/>
                <path d="M 31,17 L 31,29.5" stroke="#b58863"/>
                <path d="M 14,17 L 14,29.5" stroke="#b58863"/>
              </g>
            </svg>
          </div>

          <h1 style={{
            fontSize: '1.5rem',
            fontWeight: '400',
            letterSpacing: '0.3em',
            margin: '0 0 2rem 0',
            color: '#e0e0e0',
            textTransform: 'uppercase',
            textAlign: 'center',
          }}>
            CheckMate
          </h1>

          <h2 style={{
            ...menuStyles.title,
            fontSize: '1.2rem',
          }}>
            Authentication Required
          </h2>
          <p style={menuStyles.subtitle}>You must be logged in to play ranked games.</p>
          <button 
            onClick={() => navigate('/login')}
            style={menuStyles.accentButton}
          >
            Log In
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={menuStyles.container}>
      {/* User info bar at top if logged in */}
      {isLoggedIn && (
        <div style={{
          position: 'absolute',
          top: 0,
          right: 0,
          padding: '1rem 2rem',
          display: 'flex',
          alignItems: 'center',
          gap: '1rem',
        }}>
          <span style={{ color: '#e0e0e0' }}>
            {userName || userEmail}
          </span>
          <button
            onClick={() => navigate('/profile')}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#3a3a3a',
              color: '#e0e0e0',
              border: '1px solid #4a4a4a',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.9rem',
            }}
          >
            Profile
          </button>
          <button
            onClick={handleLogout}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: 'transparent',
              color: '#999',
              border: '1px solid #3a3a3a',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.9rem',
            }}
          >
            Logout
          </button>
        </div>
      )}

      <button onClick={() => navigate('/')} style={menuStyles.backButton}>
        ← Back
      </button>
      <div style={{ ...menuStyles.card, maxWidth: '400px' }}>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center',
          marginBottom: '1rem' 
        }}>
          <svg width="80" height="80" viewBox="0 0 45 45" xmlns="http://www.w3.org/2000/svg">
            <g fill="none" fillRule="evenodd" stroke="#b58863" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M 9,39 L 36,39 L 36,36 L 9,36 L 9,39 z" fill="#b58863"/>
              <path d="M 12,36 L 12,32 L 33,32 L 33,36 L 12,36 z" fill="#b58863"/>
              <path d="M 11,14 L 11,9 L 15,9 L 15,11 L 20,11 L 20,9 L 25,9 L 25,11 L 30,11 L 30,9 L 34,9 L 34,14" fill="#b58863"/>
              <path d="M 34,14 L 31,17 L 31,29.5 L 33,32 L 12,32 L 14,29.5 L 14,17 L 11,14" fill="#b58863"/>
              <path d="M 31,17 L 31,29.5" stroke="#b58863"/>
              <path d="M 14,17 L 14,29.5" stroke="#b58863"/>
            </g>
          </svg>
        </div>

        <h1 style={{
          fontSize: '1.5rem',
          fontWeight: '400',
          letterSpacing: '0.3em',
          margin: '0 0 2rem 0',
          color: '#e0e0e0',
          textTransform: 'uppercase',
          textAlign: 'center',
        }}>
          CheckMate
        </h1>

        <h2 style={{
          ...menuStyles.title,
          fontSize: '1.2rem',
        }}>
          Ranked Game
        </h2>
        <p style={menuStyles.subtitle}>Select a time control to find an opponent</p>
        
        <div style={menuStyles.infoBox}>
          <span style={{ color: '#999', marginRight: '0.5rem' }}>Your Rating:</span>
          <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#c9a068' }}>
            {user.eloRating || 1500}
          </span>
        </div>

        <TimeControlSelector 
          onSelect={handleTimeControlSelect}
          disabled={isSearching}
        />

        {isSearching && selectedTimeControl && (
          <MatchmakingModal
            timeControl={selectedTimeControl}
            onCancel={handleCancelSearch}
            onGameFound={handleGameFound}
          />
        )}
      </div>
    </div>
  );
};

export default RankedGamePage;
