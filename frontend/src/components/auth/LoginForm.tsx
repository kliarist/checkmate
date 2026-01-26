import React, { useState } from 'react';

interface LoginFormProps {
  onSubmit: (email: string, password: string) => void;
  loading?: boolean;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSubmit, loading = false }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    // Email validation
    if (!email) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = 'Invalid email format';
    }

    // Password validation
    if (!password) {
      newErrors.password = 'Password is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (validateForm()) {
      onSubmit(email, password);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div style={{ marginBottom: '1rem' }}>
        <label htmlFor="email" style={{ display: 'block', marginBottom: '0.5rem', color: '#e0e0e0' }}>
          Email
        </label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={{
            width: '100%',
            padding: '0.75rem',
            borderRadius: '4px',
            border: errors.email ? '1px solid #ff6b6b' : '1px solid #444',
            backgroundColor: '#2a2a2a',
            color: '#e0e0e0',
            fontSize: '1rem',
            boxSizing: 'border-box',
          }}
          disabled={loading}
        />
        {errors.email && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.email}
          </span>
        )}
      </div>

      <div style={{ marginBottom: '1.5rem' }}>
        <label htmlFor="password" style={{ display: 'block', marginBottom: '0.5rem', color: '#e0e0e0' }}>
          Password
        </label>
        <input
          type="password"
          id="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{
            width: '100%',
            padding: '0.75rem',
            borderRadius: '4px',
            border: errors.password ? '1px solid #ff6b6b' : '1px solid #444',
            backgroundColor: '#2a2a2a',
            color: '#e0e0e0',
            fontSize: '1rem',
            boxSizing: 'border-box',
          }}
          disabled={loading}
        />
        {errors.password && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.password}
          </span>
        )}
      </div>

      <button
        type="submit"
        disabled={loading}
        style={{
          width: '100%',
          padding: '0.75rem',
          backgroundColor: loading ? '#555' : '#b58863',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          fontSize: '1rem',
          fontWeight: '600',
          cursor: loading ? 'not-allowed' : 'pointer',
        }}
      >
        {loading ? 'Logging in...' : 'Log In'}
      </button>
    </form>
  );
};
