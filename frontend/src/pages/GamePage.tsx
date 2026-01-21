import { useParams } from 'react-router-dom';
import { ChessBoard } from '../components/game/ChessBoard';
import { MoveList } from '../components/game/MoveList';
import { ChatPanel } from '../components/game/ChatPanel';
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

  const handleOfferDraw = () => {
    // TODO: Implement draw offer functionality
    alert('Draw offer functionality coming soon!');
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
        Loading game...
      </div>
    );
  }

  if (error && !fen) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#242424',
        textAlign: 'center',
        padding: '2rem',
        color: '#ff6b6b',
      }}>
        Error loading game: {error}
      </div>
    );
  }

  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: '1fr auto 380px',
      gap: '2rem',
      height: '100vh',
      backgroundColor: '#242424',
      padding: '2rem',
      boxSizing: 'border-box',
    }}>
      {/* Left spacer */}
      <div />

      {/* Center - Chess Board */}
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
      }}>
        {error && (
          <div style={{
            backgroundColor: '#ffebee',
            color: '#c62828',
            padding: '0.75rem 1rem',
            borderRadius: '4px',
            marginBottom: '1rem',
            fontSize: '0.9rem',
          }}>
            {error}
          </div>
        )}

        <div style={{
          marginBottom: '1rem',
          color: '#999',
          fontSize: '0.9rem',
        }}>
          Playing as {playerColor === 'white' ? '⚪ White' : '⚫ Black'}
        </div>

        <ChessBoard
          fen={fen}
          onMove={makeMove}
          playerColor={playerColor}
          onResign={resign}
          onOfferDraw={handleOfferDraw}
        />
      </div>

      {/* Right Sidebar - Moves and Chat */}
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem',
        height: 'calc(100vh - 4rem)',
      }}>
        {/* Move History */}
        <div style={{
          backgroundColor: '#2a2a2a',
          borderRadius: '4px',
          padding: '1rem',
          height: '40%',
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
        }}>
          <h3 style={{
            margin: '0 0 1rem 0',
            color: '#e0e0e0',
            fontSize: '1rem',
            fontWeight: '500',
          }}>
            Move History
          </h3>
          <div style={{
            flex: 1,
            overflowY: 'auto',
          }}>
            <MoveList moves={moves} />
          </div>
        </div>

        {/* Chat Panel */}
        <div style={{
          height: '60%',
          overflow: 'hidden',
        }}>
          <ChatPanel />
        </div>
      </div>

      {isGameOver && <GameEndModal result={result} onClose={() => window.location.href = '/'} />}
    </div>
  );
};
