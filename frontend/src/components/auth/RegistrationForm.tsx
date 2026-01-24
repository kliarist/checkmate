import React, { useState } from 'react';

interface RegistrationFormProps {
  onSubmit: (email: string, username: string, password: string) => void;
  loading?: boolean;
}

export const RegistrationForm: React.FC<RegistrationFormProps> = ({ onSubmit, loading = false }) => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    // Email validation
    if (!email) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = 'Invalid email format';
    }

    // Username validation
    if (!username) {
      newErrors.username = 'Username is required';
    } else if (username.length < 3 || username.length > 20) {
      newErrors.username = 'Username must be between 3 and 20 characters';
    }

    // Password validation
    if (!password) {
      newErrors.password = 'Password is required';
    } else if (password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters';
    } else if (!/\d/.test(password)) {
      newErrors.password = 'Password must contain at least one number';
    }

    // Confirm password validation
    if (password !== confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (validateForm()) {
      onSubmit(email, username, password);
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
          }}
          disabled={loading}
        />
        {errors.email && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.email}
          </span>
        )}
      </div>

      <div style={{ marginBottom: '1rem' }}>
        <label htmlFor="username" style={{ display: 'block', marginBottom: '0.5rem', color: '#e0e0e0' }}>
          Username
        </label>
        <input
          type="text"
          id="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{
            width: '100%',
            padding: '0.75rem',
            borderRadius: '4px',
            border: errors.username ? '1px solid #ff6b6b' : '1px solid #444',
            backgroundColor: '#2a2a2a',
            color: '#e0e0e0',
            fontSize: '1rem',
          }}
          disabled={loading}
        />
        {errors.username && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.username}
          </span>
        )}
      </div>

      <div style={{ marginBottom: '1rem' }}>
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
          }}
          disabled={loading}
        />
        {errors.password && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.password}
          </span>
        )}
      </div>

      <div style={{ marginBottom: '1.5rem' }}>
        <label htmlFor="confirmPassword" style={{ display: 'block', marginBottom: '0.5rem', color: '#e0e0e0' }}>
          Confirm Password
        </label>
        <input
          type="password"
          id="confirmPassword"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          style={{
            width: '100%',
            padding: '0.75rem',
            borderRadius: '4px',
            border: errors.confirmPassword ? '1px solid #ff6b6b' : '1px solid #444',
            backgroundColor: '#2a2a2a',
            color: '#e0e0e0',
            fontSize: '1rem',
          }}
          disabled={loading}
        />
        {errors.confirmPassword && (
          <span style={{ color: '#ff6b6b', fontSize: '0.85rem', marginTop: '0.25rem', display: 'block' }}>
            {errors.confirmPassword}
          </span>
        )}
      </div>

      <button
        type="submit"
        disabled={loading}
        style={{
          width: '100%',
          padding: '0.75rem',
          backgroundColor: loading ? '#555' : '#4a9eff',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          fontSize: '1rem',
          fontWeight: '600',
          cursor: loading ? 'not-allowed' : 'pointer',
        }}
      >
        {loading ? 'Creating Account...' : 'Register'}
      </button>
    </form>
  );
};
