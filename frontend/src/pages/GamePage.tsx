import { useParams } from 'react-router-dom';
import { ChessBoard } from '../components/game/ChessBoard';
import { MoveList } from '../components/game/MoveList';
import { GameEndModal } from '../components/game/GameEndModal';
import { useChessGame } from '../hooks/useChessGame';

export const GamePage = () => {
  const { id } = useParams<{ id: string }>();
  const {
    fen,
    moves,
    isGameOver,
    result,
    makeMove,
    resign,
    loading,
    error,
    playerColor,
  } = useChessGame(id!);

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '400px',
        padding: '2rem'
      }}>
        Loading game...
      </div>
    );
  }

  if (error && !fen) {
    return (
      <div style={{ textAlign: 'center', padding: '2rem', color: 'red' }}>
        Error loading game: {error}
      </div>
    );
  }

  return (
    <div style={{
      display: 'flex',
      gap: '2rem',
      padding: '2rem',
      maxWidth: '1200px',
      margin: '0 auto',
      minHeight: '850px', // Prevent layout shift
    }}>
      <div style={{
        flex: 1,
        minWidth: 0, // Prevent flex item from overflowing
        display: 'flex',
        flexDirection: 'column',
      }}>
        {/* Fixed height error container to prevent layout shift */}
        <div style={{
          minHeight: '60px',
          marginBottom: '0.5rem',
        }}>
          {error && (
            <div style={{
              backgroundColor: '#ffebee',
              color: '#c62828',
              padding: '1rem',
              borderRadius: '4px',
              textAlign: 'center'
            }}>
              {error}
            </div>
          )}
        </div>

        <div style={{ marginBottom: '1rem', fontWeight: 'bold' }}>
          You are playing as: {playerColor.charAt(0).toUpperCase() + playerColor.slice(1)}
        </div>

        <ChessBoard fen={fen} onMove={makeMove} playerColor={playerColor} />

        <div style={{ marginTop: '1rem', textAlign: 'center' }}>
          <button
            onClick={resign}
            style={{
              padding: '0.5rem 2rem',
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '1rem',
            }}
          >
            Resign
          </button>
        </div>
      </div>

      <div style={{
        width: '300px',
        flexShrink: 0,
      }}>
        <h3 style={{ marginTop: 0 }}>Move History</h3>
        <MoveList moves={moves} />
      </div>

      {isGameOver && <GameEndModal result={result} onClose={() => window.location.href = '/'} />}
    </div>
  );
};
