import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/client';

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
  const [mode, setMode] = useState<'main' | 'signin' | 'signup'>('main');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handlePlayAnonymously = async () => {
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

  const handleSignIn = async () => {
    setLoading(true);
    setError('');

    try {
      // TODO: Implement sign in API call
      setError('Sign in functionality coming soon!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to sign in');
    } finally {
      setLoading(false);
    }
  };

  const handleSignUp = async () => {
    setLoading(true);
    setError('');

    try {
      // TODO: Implement sign up API call
      setError('Sign up functionality coming soon!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to sign up');
    } finally {
      setLoading(false);
    }
  };

  // Main menu view
  if (mode === 'main') {
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
          <RookLogo />
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

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', width: '280px' }}>
          <button
            onClick={() => setMode('signin')}
            style={{
              width: '100%',
              padding: '0.875rem',
              fontSize: '1rem',
              fontWeight: '500',
              backgroundColor: '#b58863',
              color: '#fff',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            Sign In
          </button>

          <button
            onClick={() => setMode('signup')}
            style={{
              width: '100%',
              padding: '0.875rem',
              fontSize: '1rem',
              fontWeight: '500',
              backgroundColor: '#3a3a3a',
              color: '#e0e0e0',
              border: '1px solid #4a4a4a',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            Sign Up
          </button>

          <div style={{
            textAlign: 'center',
            margin: '0.5rem 0',
            color: '#666',
            fontSize: '0.85rem',
          }}>
            or
          </div>

          <button
            onClick={handlePlayAnonymously}
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.875rem',
              fontSize: '1rem',
              fontWeight: '500',
              backgroundColor: 'transparent',
              color: '#999',
              border: '1px solid #3a3a3a',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
            }}
          >
            {loading ? '...' : 'Play Anonymously'}
          </button>
        </div>

        {error && (
          <div style={{
            color: '#ff6b6b',
            marginTop: '1rem',
            fontSize: '0.85rem',
          }}>
            {error}
          </div>
        )}
      </div>
    );
  }

  // Sign In view
  if (mode === 'signin') {
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
        <div style={{ width: '300px' }}>
          <button
            onClick={() => { setMode('main'); setError(''); }}
            style={{
              background: 'none',
              border: 'none',
              color: '#999',
              cursor: 'pointer',
              fontSize: '0.9rem',
              marginBottom: '2rem',
              padding: 0,
            }}
          >
            ← Back
          </button>

          <h2 style={{
            fontSize: '1.5rem',
            fontWeight: '400',
            color: '#e0e0e0',
            marginBottom: '2rem',
          }}>
            Sign In
          </h2>

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
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.75rem 1rem',
              fontSize: '1rem',
              backgroundColor: '#2a2a2a',
              border: '1px solid #3a3a3a',
              borderRadius: '4px',
              color: '#e0e0e0',
              outline: 'none',
              marginBottom: '0.75rem',
              boxSizing: 'border-box',
            }}
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.75rem 1rem',
              fontSize: '1rem',
              backgroundColor: '#2a2a2a',
              border: '1px solid #3a3a3a',
              borderRadius: '4px',
              color: '#e0e0e0',
              outline: 'none',
              marginBottom: '1rem',
              boxSizing: 'border-box',
            }}
          />

          <button
            onClick={handleSignIn}
            disabled={loading || !email || !password}
            style={{
              width: '100%',
              padding: '0.875rem',
              fontSize: '1rem',
              fontWeight: '500',
              backgroundColor: loading || !email || !password ? '#3a3a3a' : '#b58863',
              color: '#fff',
              border: 'none',
              borderRadius: '4px',
              cursor: loading || !email || !password ? 'not-allowed' : 'pointer',
            }}
          >
            {loading ? '...' : 'Sign In'}
          </button>
        </div>

        <style>{`
          ::placeholder { color: #666; }
          input:focus { border-color: #b58863; }
        `}</style>
      </div>
    );
  }

  // Sign Up view
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
      <div style={{ width: '300px' }}>
        <button
          onClick={() => { setMode('main'); setError(''); }}
          style={{
            background: 'none',
            border: 'none',
            color: '#999',
            cursor: 'pointer',
            fontSize: '0.9rem',
            marginBottom: '2rem',
            padding: 0,
          }}
        >
          ← Back
        </button>

        <h2 style={{
          fontSize: '1.5rem',
          fontWeight: '400',
          color: '#e0e0e0',
          marginBottom: '2rem',
        }}>
          Sign Up
        </h2>

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
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          disabled={loading}
          style={{
            width: '100%',
            padding: '0.75rem 1rem',
            fontSize: '1rem',
            backgroundColor: '#2a2a2a',
            border: '1px solid #3a3a3a',
            borderRadius: '4px',
            color: '#e0e0e0',
            outline: 'none',
            marginBottom: '0.75rem',
            boxSizing: 'border-box',
          }}
        />

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={loading}
          style={{
            width: '100%',
            padding: '0.75rem 1rem',
            fontSize: '1rem',
            backgroundColor: '#2a2a2a',
            border: '1px solid #3a3a3a',
            borderRadius: '4px',
            color: '#e0e0e0',
            outline: 'none',
            marginBottom: '0.75rem',
            boxSizing: 'border-box',
          }}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={loading}
          style={{
            width: '100%',
            padding: '0.75rem 1rem',
            fontSize: '1rem',
            backgroundColor: '#2a2a2a',
            border: '1px solid #3a3a3a',
            borderRadius: '4px',
            color: '#e0e0e0',
            outline: 'none',
            marginBottom: '1rem',
            boxSizing: 'border-box',
          }}
        />

        <button
          onClick={handleSignUp}
          disabled={loading || !username || !email || !password}
          style={{
            width: '100%',
            padding: '0.875rem',
            fontSize: '1rem',
            fontWeight: '500',
            backgroundColor: loading || !username || !email || !password ? '#3a3a3a' : '#b58863',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            cursor: loading || !username || !email || !password ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? '...' : 'Sign Up'}
        </button>
      </div>

      <style>{`
        ::placeholder { color: #666; }
        input:focus { border-color: #b58863; }
      `}</style>
    </div>
  );
};
