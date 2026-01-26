import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RegistrationForm } from '../components/auth/RegistrationForm';
import apiClient from '../api/client';

// Rook SVG logo component - same as splash screen
const RookLogo = () => (
  <svg width="60" height="60" viewBox="0 0 45 45" xmlns="http://www.w3.org/2000/svg">
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

export const RegistrationPage: React.FC = () => {
  const navigate = useNavigate();
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const handleRegister = async (email: string, username: string, password: string) => {
    setError('');
    setLoading(true);

    try {
      const response = await apiClient.post('/api/auth/register', {
        email,
        username,
        password,
      });

      const { token, userId, email: userEmail, username: userName } = response.data;

      // Store auth data
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId.toString());
      localStorage.setItem('userEmail', userEmail);
      localStorage.setItem('username', userName);

      // Redirect to home or game lobby
      navigate('/');
    } catch (err: any) {
      const message = err.response?.data?.message || 'Registration failed. Please try again.';
      setError(message);
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
      padding: '2rem',
    }}>
      <div style={{
        maxWidth: '400px',
        width: '100%',
      }}>
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
          margin: '0 0 2rem 0',
          color: '#e0e0e0',
          textTransform: 'uppercase',
          textAlign: 'center',
        }}>
          CheckMate
        </h1>

        <h2 style={{
          textAlign: 'center',
          marginBottom: '2rem',
          color: '#e0e0e0',
          fontSize: '1.2rem',
          fontWeight: '400',
        }}>
          Create Account
        </h2>

        {error && (
          <div style={{
            padding: '0.75rem',
            marginBottom: '1rem',
            backgroundColor: 'rgba(255, 107, 107, 0.1)',
            color: '#ff6b6b',
            border: '1px solid rgba(255, 107, 107, 0.3)',
            borderRadius: '4px',
            fontSize: '0.9rem',
          }}>
            {error}
          </div>
        )}

        <RegistrationForm onSubmit={handleRegister} loading={loading} />

        <p style={{
          textAlign: 'center',
          marginTop: '1.5rem',
          color: '#999',
        }}>
          Already have an account?{' '}
          <a
            href="/login"
            style={{
              color: '#b58863',
              textDecoration: 'none',
            }}
          >
            Sign In
          </a>
        </p>

        <p style={{
          textAlign: 'center',
          marginTop: '1rem',
        }}>
          <a
            href="/"
            style={{
              color: '#666',
              textDecoration: 'none',
              fontSize: '0.9rem',
            }}
          >
            ← Back to Home
          </a>
        </p>
      </div>
    </div>
  );
};
