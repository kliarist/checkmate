import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RegistrationForm } from '../components/auth/RegistrationForm';
import apiClient from '../api/client';

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
          Create Account
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
              color: '#4a9eff',
              textDecoration: 'none',
            }}
          >
            Log in
          </a>
        </p>
      </div>
    </div>
  );
};
