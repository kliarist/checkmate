import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserStatsCard } from '../components/profile/UserStatsCard';
import { GameHistoryList } from '../components/profile/GameHistoryList';
import apiClient from '../api/client';

interface UserProfile {
  userId: number;
  email: string;
  username: string;
  eloRating: number;
  gamesPlayed: number;
  wins: number;
  losses: number;
  draws: number;
  createdAt: string;
}

export const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    try {
      const response = await apiClient.get('/api/users/me');
      setProfile(response.data);
    } catch (err: any) {
      if (err.response?.status === 401) {
        localStorage.clear();
        navigate('/login');
      } else {
        setError('Failed to load profile');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#242424',
        color: '#e0e0e0',
      }}>
        Loading profile...
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#242424',
        color: '#ff6b6b',
      }}>
        {error || 'Failed to load profile'}
      </div>
    );
  }

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#242424',
      padding: '2rem',
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
      }}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '2rem',
        }}>
          <div>
            <h1 style={{ color: '#e0e0e0', margin: 0 }}>
              {profile.username}'s Profile
            </h1>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '1rem',
              marginTop: '0.5rem',
            }}>
              <span style={{ color: '#999', fontSize: '0.9rem' }}>Rating:</span>
              <span style={{
                fontSize: '2rem',
                fontWeight: '700',
                color: '#c9a068',
                fontFamily: 'monospace',
              }}>
                {profile.eloRating}
              </span>
            </div>
          </div>
          <button
            onClick={handleLogout}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#ff6b6b',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontWeight: '600',
            }}
          >
            Logout
          </button>
        </div>

        <UserStatsCard
          eloRating={profile.eloRating}
          gamesPlayed={profile.gamesPlayed}
          wins={profile.wins}
          losses={profile.losses}
          draws={profile.draws}
        />

        <div style={{ marginTop: '2rem' }}>
          <h2 style={{ color: '#e0e0e0', marginBottom: '1rem' }}>
            Game History
          </h2>
          <GameHistoryList userId={profile.userId} />
        </div>
      </div>
    </div>
  );
};
