import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

// Knight SVG logo component - same as favicon
const KnightLogo = () => (
  <svg width="80" height="80" viewBox="0 0 45 45" xmlns="http://www.w3.org/2000/svg">
    <g transform="translate(2, 2)" fill="none" fillRule="evenodd" stroke="#b58863" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
      <path d="M 22,10 C 32.5,11 38.5,18 38,39 L 15,39 C 15,30 25,32.5 23,18" fill="#b58863"/>
      <path d="M 24,18 C 24.38,20.91 18.45,25.37 16,27 C 13,29 13.18,31.34 11,31 C 9.958,30.06 12.41,27.96 11,28 C 10,28 11.19,29.23 10,30 C 9,30 5.997,31 6,26 C 6,24 12,14 12,14 C 12,14 13.89,12.1 14,10.5 C 13.27,9.506 13.5,8.5 13.5,7.5 C 14.5,6.5 16.5,10 16.5,10 L 18.5,10 C 18.5,10 19.28,8.008 21,7 C 22,7 22,10 22,10" fill="#b58863"/>
      <path d="M 9.5 25.5 A 0.5 0.5 0 1 1 8.5,25.5 A 0.5 0.5 0 1 1 9.5 25.5 z" fill="#242424"/>
      <path d="M 15 15.5 A 0.5 1.5 0 1 1 14,15.5 A 0.5 1.5 0 1 1 15 15.5 z" fill="#242424" transform="matrix(0.866,0.5,-0.5,0.866,9.693,-5.173)"/>
    </g>
  </svg>
);

export const GuestLandingPage = () => {
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handlePlayAsGuest = async () => {
    setLoading(true);
    setError('');
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    try {
      const response = await apiClient.post('/api/games/guest', {
        guestUsername: username || null,
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

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#242424',
      margin: 0,
      padding: 0,
    }}>
      <div style={{ marginBottom: '1rem' }}>
        <KnightLogo />
      </div>

      <h1 style={{
        fontSize: '1.5rem',
        fontWeight: '400',
        letterSpacing: '0.3em',
        margin: '0 0 3rem 0',
        color: '#e0e0e0',
        textTransform: 'uppercase',
      }}>
        CheckMate
      </h1>

      {error && (
        <div style={{
          color: '#ff6b6b',
          marginBottom: '1rem',
          fontSize: '0.85rem',
        }}>
          {error}
        </div>
      )}

      <input
        type="text"
        placeholder="Your name"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        disabled={loading}
        style={{
          width: '240px',
          padding: '0.75rem 1rem',
          fontSize: '1rem',
          backgroundColor: '#2a2a2a',
          border: '1px solid #3a3a3a',
          borderRadius: '4px',
          color: '#e0e0e0',
          textAlign: 'center',
          outline: 'none',
          marginBottom: '1rem',
        }}
      />

      <button
        onClick={handlePlayAsGuest}
        disabled={loading}
        style={{
          width: '240px',
          padding: '0.75rem',
          fontSize: '1rem',
          fontWeight: '500',
          backgroundColor: loading ? '#3a3a3a' : '#b58863',
          color: '#fff',
          border: 'none',
          borderRadius: '4px',
          cursor: loading ? 'not-allowed' : 'pointer',
        }}
      >
        {loading ? '...' : 'Play'}
      </button>

      <style>{`
        ::placeholder { color: #666; }
        input:focus { border-color: #b58863; }
      `}</style>
    </div>
  );
};
