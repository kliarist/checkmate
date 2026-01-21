import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

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
      <div style={{
        fontSize: '4rem',
        marginBottom: '0.5rem',
        color: '#b58863',
      }}>♞</div>

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
