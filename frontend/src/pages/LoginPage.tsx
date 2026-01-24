import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { LoginForm } from '../components/auth/LoginForm';
import apiClient from '../api/client';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (email: string, password: string) => {
    setError('');
    setLoading(true);

    try {
      const response = await apiClient.post('/api/auth/login', {
        email,
        password,
      });

      const { token, userId, email: userEmail, username } = response.data;

      // Store auth data
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId.toString());
      localStorage.setItem('userEmail', userEmail);
      localStorage.setItem('username', username);

      // Redirect to home or game lobby
      navigate('/');
    } catch (err: any) {
      const message = err.response?.data?.message || 'Login failed. Please try again.';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      backgroundColor: '#242424',
      padding: '2rem',
    }}>
      <div style={{
        maxWidth: '400px',
        width: '100%',
        backgroundColor: '#1a1a1a',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.3)',
      }}>
        <h1 style={{
          textAlign: 'center',
          marginBottom: '2rem',
          color: '#e0e0e0',
        }}>
          Log In
        </h1>

        {error && (
          <div style={{
            padding: '0.75rem',
            marginBottom: '1rem',
            backgroundColor: '#ff6b6b',
            color: 'white',
            borderRadius: '4px',
            fontSize: '0.9rem',
          }}>
            {error}
          </div>
        )}

        <LoginForm onSubmit={handleLogin} loading={loading} />

        <p style={{
          textAlign: 'center',
          marginTop: '1.5rem',
          color: '#999',
        }}>
          Don't have an account?{' '}
          <a
            href="/register"
            style={{
              color: '#4a9eff',
              textDecoration: 'none',
            }}
          >
            Register
          </a>
        </p>
      </div>
    </div>
  );
};
