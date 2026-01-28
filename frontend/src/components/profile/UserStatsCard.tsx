import React from 'react';

interface UserStatsCardProps {
  eloRating: number;
  gamesPlayed: number;
  wins: number;
  losses: number;
  draws: number;
}

export const UserStatsCard: React.FC<UserStatsCardProps> = ({
  eloRating,
  gamesPlayed,
  wins,
  losses,
  draws,
}) => {
  const winRate = gamesPlayed > 0 ? ((wins / gamesPlayed) * 100).toFixed(1) : '0.0';

  return (
    <div style={{
      backgroundColor: '#1a1a1a',
      padding: '2rem',
      borderRadius: '8px',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.3)',
    }}>
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
        gap: '1.5rem',
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#4a9eff',
            marginBottom: '0.5rem',
          }}>
            {eloRating}
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            ELO Rating
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#e0e0e0',
            marginBottom: '0.5rem',
          }}>
            {gamesPlayed}
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            Games Played
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#c9a068',
            marginBottom: '0.5rem',
          }}>
            {wins}
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            Wins
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#ff6b6b',
            marginBottom: '0.5rem',
          }}>
            {losses}
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            Losses
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#ffa726',
            marginBottom: '0.5rem',
          }}>
            {draws}
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            Draws
          </div>
        </div>

        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontSize: '2.5rem',
            fontWeight: 'bold',
            color: '#9c27b0',
            marginBottom: '0.5rem',
          }}>
            {winRate}%
          </div>
          <div style={{ color: '#999', fontSize: '0.9rem' }}>
            Win Rate
          </div>
        </div>
      </div>
    </div>
  );
};
