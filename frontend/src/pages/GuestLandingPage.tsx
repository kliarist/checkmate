import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

export const GuestLandingPage = () => {
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handlePlayAsGuest = async () => {
    setLoading(true);
    try {
      const response = await apiClient.post('/api/games/guest', {
        guestUsername: username || null,
      });
      const { gameId } = response.data.data;
      navigate(`/game/${gameId}`);
    } catch (error) {
      console.error('Failed to create game:', error);
      alert('Failed to create game. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ textAlign: 'center', padding: '4rem' }}>
      <h1>Welcome to Checkmate Chess</h1>
      <p>Play chess instantly without registration</p>

      <div style={{ marginTop: '2rem' }}>
        <input
          type="text"
          placeholder="Enter your name (optional)"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{ padding: '0.5rem', fontSize: '1rem', marginRight: '1rem' }}
        />
        <button
          onClick={handlePlayAsGuest}
          disabled={loading}
          style={{
            padding: '0.5rem 2rem',
            fontSize: '1rem',
            backgroundColor: '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? 'Creating Game...' : 'Play as Guest'}
        </button>
      </div>
    </div>
  );
};

