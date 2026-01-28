import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';
import { menuStyles } from '../styles/menuStyles';

// Rook SVG logo component - same as favicon
const RookLogo = () => (
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
);

export const GuestLandingPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Check if user is logged in
  const userEmail = localStorage.getItem('userEmail');
  const userName = localStorage.getItem('username');
  const isLoggedIn = !!localStorage.getItem('token');

  const handlePlayAnonymously = async () => {
    setLoading(true);
    setError('');
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    try {
      const response = await apiClient.post('/api/games/guest', {
        guestUsername: 'Guest',
      });
      const { gameId, guestUserId, token } = response.data.data;

      if (token) localStorage.setItem('token', token);
      if (guestUserId) localStorage.setItem('guestUserId', guestUserId);

      navigate(`/game/${gameId}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create game');
    } finally {
      setLoading(false);
    }
  };

  const handlePlayComputer = () => {
    navigate('/computer');
  };

  const handlePlayPrivate = () => {
    navigate('/private');
  };

  const handlePlayRanked = () => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }
    navigate('/ranked');
  };

  const handleSignIn = () => {
    navigate('/login');
  };

  const handleSignUp = () => {
    navigate('/register');
  };

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  // Main menu view
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

      <div style={{ ...menuStyles.card, maxWidth: '400px' }}>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center',
          marginBottom: '1rem' 
        }}>
          <RookLogo />
        </div>

        <h1 style={{
          fontSize: '1.5rem',
          fontWeight: '400',
          letterSpacing: '0.3em',
          margin: '0 0 3rem 0',
          color: '#e0e0e0',
          textTransform: 'uppercase',
          textAlign: 'center',
        }}>
          CheckMate
        </h1>

        {!isLoggedIn && (
          <>
            <button
              onClick={handleSignIn}
              style={menuStyles.signInButton}
            >
              Sign In
            </button>

            <button
              onClick={handleSignUp}
              style={{ ...menuStyles.signUpButton, marginTop: '0.75rem' }}
            >
              Sign Up
            </button>

            <div style={{
              textAlign: 'center',
              margin: '1.5rem 0 1rem 0',
              color: '#666',
              fontSize: '0.85rem',
            }}>
              or play as guest
            </div>
          </>
        )}

        {/* Game Mode Buttons */}
        <button
          onClick={handlePlayComputer}
          style={{ ...menuStyles.computerButton, marginTop: isLoggedIn ? '0' : '0.75rem' }}
        >
          🤖 Play vs Computer
        </button>

        <button
          onClick={handlePlayPrivate}
          style={{ ...menuStyles.privateButton, marginTop: '0.75rem' }}
        >
          👥 Play with Friend
        </button>

        {isLoggedIn && (
          <button
            onClick={handlePlayRanked}
            style={{ ...menuStyles.rankedButton, marginTop: '0.75rem' }}
          >
            🏆 Ranked Game
          </button>
        )}

        {!isLoggedIn && (
          <button
            onClick={handlePlayAnonymously}
            disabled={loading}
            style={{ 
              ...(loading ? menuStyles.guestButtonDisabled : menuStyles.guestButton),
              marginTop: '0.75rem'
            }}
          >
            {loading ? '...' : '🎲 Quick Play (Anonymous)'}
          </button>
        )}

        {error && (
          <div style={{
            ...menuStyles.errorBox,
            marginTop: '1rem',
            fontSize: '0.85rem',
          }}>
            {error}
          </div>
        )}
      </div>
    </div>
  );
};
