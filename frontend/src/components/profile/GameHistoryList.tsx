import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../api/client';
import { downloadPgn } from '../../api/gameApi';

interface Game {
  id: string;
  whitePlayerId: number;
  blackPlayerId: number;
  status: string;
  result: string | null;
  endReason: string | null;
  createdAt: string;
  endedAt: string | null;
}

interface GameHistoryListProps {
  userId: number;
}

export const GameHistoryList: React.FC<GameHistoryListProps> = ({ userId }) => {
  const navigate = useNavigate();
  const [games, setGames] = useState<Game[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    loadGames();
  }, [page]);

  const loadGames = async () => {
    try {
      const response = await apiClient.get(`/api/users/me/games?page=${page}&size=20`);
      const newGames = response.data.content || [];
      
      setGames(prev => page === 0 ? newGames : [...prev, ...newGames]);
      setHasMore(!response.data.last);
    } catch (err) {
      console.error('Failed to load games:', err);
    } finally {
      setLoading(false);
    }
  };

  const getGameResult = (game: Game): { text: string; color: string } => {
    if (game.status !== 'COMPLETED') {
      return { text: 'In Progress', color: '#4a9eff' };
    }

    const isWhite = game.whitePlayerId === userId;
    
    if (game.result === 'DRAW') {
      return { text: 'Draw', color: '#ffa726' };
    }

    const won = (game.result === 'WHITE_WINS' && isWhite) || 
                (game.result === 'BLACK_WINS' && !isWhite);
    
    return won 
      ? { text: 'Won', color: '#c9a068' }
      : { text: 'Lost', color: '#ff6b6b' };
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const handleExportPgn = async (e: React.MouseEvent, gameId: string) => {
    e.stopPropagation();
    try {
      await downloadPgn(gameId);
    } catch (err) {
      console.error('Failed to export PGN:', err);
    }
  };

  if (loading && page === 0) {
    return <div style={{ color: '#999' }}>Loading games...</div>;
  }

  if (games.length === 0) {
    return (
      <div style={{
        textAlign: 'center',
        padding: '3rem',
        color: '#999',
        backgroundColor: '#1a1a1a',
        borderRadius: '8px',
      }}>
        No games played yet. Start playing to build your history!
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        {games.map((game) => {
          const result = getGameResult(game);
          const isWhite = game.whitePlayerId === userId;

          return (
            <div
              key={game.id}
              onClick={() => navigate(`/game/${game.id}`)}
              style={{
                backgroundColor: '#1a1a1a',
                padding: '1rem',
                borderRadius: '4px',
                cursor: 'pointer',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                transition: 'background-color 0.2s',
              }}
              onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#252525'}
              onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#1a1a1a'}
            >
              <div>
                <div style={{ color: '#e0e0e0', marginBottom: '0.25rem' }}>
                  Playing as {isWhite ? 'White' : 'Black'}
                </div>
                <div style={{ color: '#999', fontSize: '0.85rem' }}>
                  {formatDate(game.createdAt)}
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                {game.status === 'COMPLETED' && (
                  <button
                    onClick={(e) => handleExportPgn(e, game.id)}
                    style={{
                      padding: '0.5rem 0.75rem',
                      backgroundColor: '#2a2a2a',
                      color: '#c9a068',
                      border: '1px solid #444',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '0.85rem',
                      fontWeight: '600',
                      transition: 'background-color 0.2s',
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#333'}
                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#2a2a2a'}
                  >
                    Export PGN
                  </button>
                )}
                <div style={{
                  padding: '0.5rem 1rem',
                  borderRadius: '4px',
                  backgroundColor: result.color + '20',
                  color: result.color,
                  fontWeight: '600',
                }}>
                  {result.text}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {hasMore && (
        <button
          onClick={() => setPage(p => p + 1)}
          disabled={loading}
          style={{
            width: '100%',
            marginTop: '1rem',
            padding: '0.75rem',
            backgroundColor: loading ? '#555' : '#2a2a2a',
            color: '#e0e0e0',
            border: '1px solid #444',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? 'Loading...' : 'Load More'}
        </button>
      )}
    </div>
  );
};
