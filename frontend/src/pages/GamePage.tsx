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
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      width: '100vw',
      height: '100vh',
      backgroundColor: '#242424',
      padding: '2rem',
      boxSizing: 'border-box',
      overflow: 'hidden',
    }}>
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'minmax(auto, 700px) minmax(300px, 350px)',
        gap: '2rem',
        maxWidth: '1400px',
      }}>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
        }}>
          <ChessBoard
            fen={fen}
            onMove={makeMove}
            playerColor={playerColor}
            onResign={resign}
            onOfferDraw={handleOfferDraw}
          />
        </div>

        <div style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
          height: 'calc(100vh - 4rem)',
          overflow: 'hidden',
        }}>
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

        <div style={{
          height: '60%',
          overflow: 'hidden',
        }}>
          <ChatPanel />
        </div>
      </div>

      {isGameOver && <GameEndModal result={result} onClose={() => window.location.href = '/'} />}
    </div>
    </div>
  );
};
