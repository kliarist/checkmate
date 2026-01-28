import { useParams } from 'react-router-dom';
import { ChessBoard } from '../components/game/ChessBoard';
import { MoveList } from '../components/game/MoveList';
import { ChatPanel } from '../components/game/ChatPanel';
import { GameEndModal } from '../components/game/GameEndModal';
import { PromotionDialog } from '../components/game/PromotionDialog';
import { PlayerInfo } from '../components/game/PlayerInfo';
import { GameControls } from '../components/game/GameControls';
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
    lastMove,
    isViewingHistory,
    pendingPromotion,
    handlePromotionSelect,
    whitePlayerInfo,
    blackPlayerInfo,
    gameType,
    currentTurn,
  } = useChessGame(id!);

  const handleOfferDraw = () => {
    alert('Draw offer functionality coming soon!');
  };

  const handleFlipBoard = () => {
    // Flip board functionality
  };

  const handleResignClick = () => {
    resign();
  };

  const handlePlayAgain = () => {
    // Navigate to the appropriate game creation page
    window.location.href = '/';
  };

  const handleGoToMenu = () => {
    window.location.href = '/';
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
      {/* Reserved space for history banner - always present to avoid layout shift */}
      <div style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        height: '2.5rem',
        backgroundColor: isViewingHistory ? 'rgba(255, 152, 0, 0.15)' : 'transparent',
        borderBottom: isViewingHistory ? '1px solid rgba(255, 152, 0, 0.3)' : 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 100,
        transition: 'all 0.2s ease',
        pointerEvents: isViewingHistory ? 'auto' : 'none',
      }}>
        {isViewingHistory && (
          <span style={{
            color: '#ff9800',
            fontSize: '0.85rem',
            fontWeight: '500',
            letterSpacing: '0.3px',
          }}>
            Viewing History • Click "Resume" to return to current position
          </span>
        )}
      </div>
      
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'minmax(auto, 700px) minmax(300px, 350px)',
        gap: '2rem',
        maxWidth: '1400px',
      }}>
        {/* Left side: Player info + Chessboard + Buttons */}
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'flex-start',
          gap: '0',
        }}>
          {/* Top player (opponent) */}
          {whitePlayerInfo && blackPlayerInfo && (
            <PlayerInfo
              username={playerColor === 'white' ? blackPlayerInfo.username : whitePlayerInfo.username}
              eloRating={playerColor === 'white' ? blackPlayerInfo.eloRating : whitePlayerInfo.eloRating}
              isComputer={gameType === 'COMPUTER' && (
                (playerColor === 'white' && blackPlayerInfo.username.startsWith('Computer')) ||
                (playerColor === 'black' && whitePlayerInfo.username.startsWith('Computer'))
              )}
              isActive={playerColor === 'white' ? currentTurn === 'black' : currentTurn === 'white'}
              color={playerColor === 'white' ? 'black' : 'white'}
              capturedPieces={playerColor === 'white' ? capturedByBlack : capturedByWhite}
              materialAdvantage={playerColor === 'white' ? -materialScore : materialScore}
            />
          )}
          
          <ChessBoard
            fen={fen}
            onMove={makeMove}
            playerColor={playerColor}
            lastMove={lastMove}
            isViewingHistory={isViewingHistory}
          />
          
          {/* Bottom player (current player) */}
          {whitePlayerInfo && blackPlayerInfo && (
            <PlayerInfo
              username={playerColor === 'white' ? whitePlayerInfo.username : blackPlayerInfo.username}
              eloRating={playerColor === 'white' ? whitePlayerInfo.eloRating : blackPlayerInfo.eloRating}
              isComputer={false}
              isActive={playerColor === 'white' ? currentTurn === 'white' : currentTurn === 'black'}
              color={playerColor}
              capturedPieces={playerColor === 'white' ? capturedByWhite : capturedByBlack}
              materialAdvantage={playerColor === 'white' ? materialScore : -materialScore}
            />
          )}
          
          {/* Game Controls */}
          <GameControls
            onFlipBoard={handleFlipBoard}
            onOfferDraw={handleOfferDraw}
            onResign={handleResignClick}
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

        {isGameOver && (
          <GameEndModal 
            result={result} 
            onPlayAgain={handlePlayAgain}
            onGoToMenu={handleGoToMenu}
          />
        )}
        
        {pendingPromotion && (
          <PromotionDialog
            isOpen={!!pendingPromotion}
            onSelect={handlePromotionSelect}
            playerColor={playerColor}
          />
        )}
      </div>
    </div>
  );
};
