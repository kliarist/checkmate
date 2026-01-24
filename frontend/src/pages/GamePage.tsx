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
    currentMoveIndex,
    isGameOver,
    result,
    makeMove,
    resign,
    goToMove,
    nextMove,
    previousMove,
    resumeGame,
    loading,
    error,
    playerColor,
    capturedByWhite,
    capturedByBlack,
    materialScore,
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
            capturedByWhite={capturedByWhite}
            capturedByBlack={capturedByBlack}
            materialScore={materialScore}
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
            height: '40%',
            overflow: 'hidden',
          }}>
            <MoveList
              moves={moves}
              currentMoveIndex={currentMoveIndex}
              onMoveClick={goToMove}
              onNext={nextMove}
              onPrevious={previousMove}
              onResume={resumeGame}
            />
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
