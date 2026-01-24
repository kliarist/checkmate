import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { Chessboard } from 'react-chessboard';
import { ResignConfirmModal } from './ResignConfirmModal';
import { CapturedPieces } from './CapturedPieces';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
  playerColor?: 'white' | 'black';
  onFlipBoard?: () => void;
  onResign?: () => void;
  onOfferDraw?: () => void;
  capturedByWhite?: string[];
  capturedByBlack?: string[];
  materialScore?: number;
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
  capturedByWhite = [],
  capturedByBlack = [],
  materialScore = 0,
}: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>(playerColor);
  const [selectedSquare, setSelectedSquare] = useState<string | null>(null);
  const [optionSquares, setOptionSquares] = useState<Record<string, React.CSSProperties>>({});
  const [showResignModal, setShowResignModal] = useState(false);
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
          const success = onMove(selectedSquare, focusedSquare);
          if (success) {
            setSelectedSquare(null);
            setOptionSquares({});
          } else {
            setSelectedSquare(focusedSquare);
            setOptionSquares({
              [focusedSquare]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
            });
          }
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
      const success = onMove(selectedSquare, square);
      if (success) {
        setSelectedSquare(null);
        setOptionSquares({});
      } else {
        setSelectedSquare(square);
        setOptionSquares({
          [square]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
        });
      }
    } else {
      setSelectedSquare(square);
      setOptionSquares({
        [square]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
      });
    }
  }, [selectedSquare, onMove]);

  const onPieceDragBegin = useCallback((_piece: string, sourceSquare: string) => {
    setSelectedSquare(sourceSquare);
    setOptionSquares({
      [sourceSquare]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
    });
  }, []);

  const onPieceDrop = useCallback((sourceSquare: string, targetSquare: string): boolean => {
    const success = onMove(sourceSquare, targetSquare);
    setSelectedSquare(null);
    setOptionSquares({});
    return success;
  }, [onMove]);

  const handleFlipBoard = useCallback(() => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
    onFlipBoard?.();
  }, [onFlipBoard]);

  const handleResignClick = useCallback(() => {
    setShowResignModal(true);
  }, []);

  const handleResignConfirm = useCallback(() => {
    setShowResignModal(false);
    onResign?.();
  }, [onResign]);

  const handleResignCancel = useCallback(() => {
    setShowResignModal(false);
  }, []);

  const boardStyle = useMemo(() => ({
    borderRadius: '4px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  }), []);

  const darkSquareStyle = useMemo(() => ({ backgroundColor: '#b58863' }), []);
  const lightSquareStyle = useMemo(() => ({ backgroundColor: '#f0d9b5' }), []);

  // Determine which captured pieces to show at top and bottom
  const topCaptured = boardOrientation === 'white'
    ? { pieces: capturedByBlack, color: 'white' as const, score: materialScore < 0 ? -materialScore : undefined }
    : { pieces: capturedByWhite, color: 'black' as const, score: materialScore > 0 ? materialScore : undefined };

  const bottomCaptured = boardOrientation === 'white'
    ? { pieces: capturedByWhite, color: 'black' as const, score: materialScore > 0 ? materialScore : undefined }
    : { pieces: capturedByBlack, color: 'white' as const, score: materialScore < 0 ? -materialScore : undefined };

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
        gap: '0.5rem',
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

      {/* Top Left Captured Pieces */}
      <div style={{ width: '100%', display: 'flex', justifyContent: 'flex-start' }}>
        <CapturedPieces
          pieces={topCaptured.pieces}
          pieceColor={topCaptured.color}
          score={topCaptured.score}
        />
      </div>

      <Chessboard
        position={fen}
        boardOrientation={boardOrientation}
        onSquareClick={onSquareClick}
        onPieceDragBegin={onPieceDragBegin}
        onPieceDrop={onPieceDrop}
        customSquareStyles={optionSquares}
        customBoardStyle={{
          ...boardStyle,
          width: '700px',
          height: '700px',
        }}
        customDarkSquareStyle={darkSquareStyle}
        customLightSquareStyle={lightSquareStyle}
        animationDuration={200}
        arePiecesDraggable={true}
      />

      {/* Bottom Left Captured Pieces */}
      <div style={{ width: '100%', display: 'flex', justifyContent: 'flex-start' }}>
        <CapturedPieces
          pieces={bottomCaptured.pieces}
          pieceColor={bottomCaptured.color}
          score={bottomCaptured.score}
        />
      </div>

      <fieldset
        style={{
          display: 'flex',
          gap: '0.5rem',
          justifyContent: 'center',
          border: 'none',
          padding: 0,
          margin: 0,
          marginTop: '0.5rem',
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
          onClick={handleResignClick}
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

      <ResignConfirmModal
        isOpen={showResignModal}
        onConfirm={handleResignConfirm}
        onCancel={handleResignCancel}
      />
    </div>
  );
};
