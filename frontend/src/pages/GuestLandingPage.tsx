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
    try {
      const response = await apiClient.post('/api/games/guest', {
        guestUsername: username || null,
      });
      const { gameId } = response.data.data;
      navigate(`/game/${gameId}`);
    } catch (err: any) {
      console.error('Failed to create game:', err);
      setError(err.response?.data?.message || 'Failed to create game. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ textAlign: 'center', padding: '4rem' }}>
      <h1>Welcome to Checkmate Chess</h1>
      <p>Play chess instantly without registration</p>

      {error && (
        <div style={{
          color: '#f44336',
          backgroundColor: '#ffebee',
          padding: '1rem',
          borderRadius: '4px',
          marginBottom: '1rem',
          maxWidth: '400px',
          margin: '1rem auto'
        }}>
          {error}
        </div>
      )}

      <div style={{ marginTop: '2rem' }}>
        <input
          type="text"
          placeholder="Enter your name (optional)"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          disabled={loading}
          style={{
            padding: '0.5rem',
            fontSize: '1rem',
            marginRight: '1rem',
            opacity: loading ? 0.6 : 1
          }}
        />
        <button
          onClick={handlePlayAsGuest}
          disabled={loading}
          style={{
            padding: '0.5rem 2rem',
            fontSize: '1rem',
            backgroundColor: loading ? '#cccccc' : '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            position: 'relative',
          }}
        >
          {loading ? (
            <>
              <span style={{ opacity: 0 }}>Play as Guest</span>
              <span style={{
                position: 'absolute',
                left: '50%',
                top: '50%',
                transform: 'translate(-50%, -50%)'
              }}>
                Creating...
              </span>
            </>
          ) : (
            'Play as Guest'
          )}
        </button>
      </div>

      {loading && (
        <div style={{ marginTop: '2rem' }}>
          <div className="spinner" style={{
            border: '4px solid #f3f3f3',
            borderTop: '4px solid #4CAF50',
            borderRadius: '50%',
            width: '40px',
            height: '40px',
            animation: 'spin 1s linear infinite',
            margin: '0 auto'
          }} />
        </div>
      )}
    </div>
  );
};

