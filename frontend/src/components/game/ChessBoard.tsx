import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
  playerColor?: 'white' | 'black';
  onFlipBoard?: () => void;
  onResign?: () => void;
  onOfferDraw?: () => void;
}

const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
const RANKS = ['1', '2', '3', '4', '5', '6', '7', '8'];

export const ChessBoard = ({
  fen,
  onMove,
  playerColor = 'white',
  onFlipBoard,
  onResign,
  onOfferDraw,
}: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>(playerColor);
  const [selectedSquare, setSelectedSquare] = useState<string | null>(null);
  const [optionSquares, setOptionSquares] = useState<Record<string, React.CSSProperties>>({});
  const [focusedSquare, setFocusedSquare] = useState<string>('e2');
  const boardContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setBoardOrientation(playerColor);
  }, [playerColor]);

  const announceSquare = useCallback((square: string) => {
    const liveRegion = document.getElementById('chess-announcer');
    if (liveRegion) {
      liveRegion.textContent = `Focused on square ${square}`;
    }
  }, []);

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (!focusedSquare) return;

    const [file, rank] = [focusedSquare[0], focusedSquare[1]];
    const fileIndex = FILES.indexOf(file);
    const rankIndex = RANKS.indexOf(rank);

    let newFileIndex = fileIndex;
    let newRankIndex = rankIndex;

    switch (e.key) {
      case 'ArrowUp':
        e.preventDefault();
        newRankIndex = Math.min(7, rankIndex + 1);
        break;
      case 'ArrowDown':
        e.preventDefault();
        newRankIndex = Math.max(0, rankIndex - 1);
        break;
      case 'ArrowLeft':
        e.preventDefault();
        newFileIndex = Math.max(0, fileIndex - 1);
        break;
      case 'ArrowRight':
        e.preventDefault();
        newFileIndex = Math.min(7, fileIndex + 1);
        break;
      case 'Enter':
      case ' ':
        e.preventDefault();
        if (selectedSquare) {
          onMove(selectedSquare, focusedSquare);
          setSelectedSquare(null);
          setOptionSquares({});
        } else {
          setSelectedSquare(focusedSquare);
          setOptionSquares({
            [focusedSquare]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
          });
        }
        break;
      case 'Escape':
        e.preventDefault();
        setSelectedSquare(null);
        setOptionSquares({});
        break;
      default:
        return;
    }

    const newSquare = FILES[newFileIndex] + RANKS[newRankIndex];
    if (newSquare !== focusedSquare) {
      setFocusedSquare(newSquare);
      announceSquare(newSquare);
    }
  }, [focusedSquare, selectedSquare, onMove, announceSquare]);

  useEffect(() => {
    const container = boardContainerRef.current;
    if (!container) return;

    container.addEventListener('keydown', handleKeyDown as any);
    return () => container.removeEventListener('keydown', handleKeyDown as any);
  }, [handleKeyDown]);

  const onSquareClick = useCallback((square: string) => {
    if (selectedSquare) {
      onMove(selectedSquare, square);
      setSelectedSquare(null);
      setOptionSquares({});
    } else {
      setSelectedSquare(square);
      setOptionSquares({
        [square]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
      });
    }
  }, [selectedSquare, onMove]);

  const handleFlipBoard = useCallback(() => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
    onFlipBoard?.();
  }, [onFlipBoard]);

  const boardStyle = useMemo(() => ({
    borderRadius: '4px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  }), []);

  const darkSquareStyle = useMemo(() => ({ backgroundColor: '#b58863' }), []);
  const lightSquareStyle = useMemo(() => ({ backgroundColor: '#f0d9b5' }), []);

  return (
    <div
      ref={boardContainerRef}
      tabIndex={0}
      role="application"
      aria-label="Chess board"
      aria-describedby="chess-instructions"
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '1rem',
        maxWidth: '700px',
        outline: 'none',
      }}
      onFocus={() => setFocusedSquare('e2')}
    >
      <div id="chess-instructions" style={{ position: 'absolute', left: '-9999px' }}>
        Use arrow keys to navigate squares. Press Enter or Space to select and move pieces. Press Escape to cancel selection.
      </div>

      <output
        id="chess-announcer"
        aria-live="polite"
        aria-atomic="true"
        style={{ position: 'absolute', left: '-9999px' }}
      />

      <Chessboard
        position={fen}
        boardOrientation={boardOrientation}
        onSquareClick={onSquareClick}
        customSquareStyles={{
          ...optionSquares,
          ...(focusedSquare ? {
            [focusedSquare]: {
              boxShadow: '0 0 0 3px #4a9eff inset',
              ...optionSquares[focusedSquare]
            }
          } : {})
        }}
        customBoardStyle={boardStyle}
        customDarkSquareStyle={darkSquareStyle}
        customLightSquareStyle={lightSquareStyle}
        animationDuration={200}
        arePiecesDraggable={false}
      />

      <fieldset
        style={{
          display: 'flex',
          gap: '0.5rem',
          justifyContent: 'center',
          border: 'none',
          padding: 0,
          margin: 0,
        }}
        aria-label="Game controls"
      >
        <button
          onClick={handleFlipBoard}
          title="Flip Board"
          aria-label="Flip board orientation"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#3a3a3a',
            color: '#e0e0e0',
            border: '2px solid transparent',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'all 0.2s',
          }}
          onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
          onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
        >
          ⇅
        </button>

        <button
          onClick={onOfferDraw}
          title="Offer Draw"
          aria-label="Offer draw to opponent"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#3a3a3a',
            color: '#e0e0e0',
            border: '2px solid transparent',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'all 0.2s',
          }}
          onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
          onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
        >
          🤝
        </button>

        <button
          onClick={onResign}
          title="Resign"
          aria-label="Resign from game"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#f44336',
            color: '#fff',
            border: '2px solid transparent',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'all 0.2s',
          }}
          onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
          onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
        >
          🏳
        </button>
      </fieldset>
    </div>
  );
};
