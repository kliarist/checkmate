import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { Chessboard } from 'react-chessboard';
import { Chess } from 'chess.js';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string, promotion?: 'q' | 'r' | 'b' | 'n') => boolean;
  playerColor?: 'white' | 'black';
  lastMove?: { from: string; to: string } | null;
  isViewingHistory?: boolean;
}

const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
const RANKS = ['1', '2', '3', '4', '5', '6', '7', '8'];

export const ChessBoard = ({
  fen,
  onMove,
  playerColor = 'white',
  lastMove = null,
  isViewingHistory = false,
}: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>(playerColor);
  const [selectedSquare, setSelectedSquare] = useState<string | null>(null);
  const [optionSquares, setOptionSquares] = useState<Record<string, React.CSSProperties>>({});
  const [focusedSquare, setFocusedSquare] = useState<string>('e2');
  const boardContainerRef = useRef<HTMLDivElement>(null);
  const chessRef = useRef(new Chess());

  // Update chess instance when FEN changes
  useEffect(() => {
    try {
      chessRef.current.load(fen);
    } catch (e) {
      console.error('Failed to load FEN:', e);
    }
  }, [fen]);

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

  const onSquareClick = useCallback(({ square }: any) => {
    if (selectedSquare) {
      const success = onMove(selectedSquare, square);
      if (success) {
        setSelectedSquare(null);
        setOptionSquares({});
      } else {
        // Try selecting the new square instead
        setSelectedSquare(square);
        
        // Get legal moves for the newly selected square
        const moves = chessRef.current.moves({ square: square as any, verbose: true });
        const newSquares: Record<string, React.CSSProperties> = {};
        
        // Highlight the selected square
        newSquares[square] = { backgroundColor: 'rgba(255, 255, 0, 0.4)' };
        
        // Highlight legal move destinations
        moves.forEach((move: any) => {
          newSquares[move.to] = {
            background: move.captured
              ? 'radial-gradient(circle, rgba(0,0,0,.1) 85%, transparent 85%)'
              : 'radial-gradient(circle, rgba(0,0,0,.1) 25%, transparent 25%)',
            borderRadius: '50%',
          };
        });
        
        setOptionSquares(newSquares);
      }
    } else {
      setSelectedSquare(square);
      
      // Get legal moves for the selected square
      const moves = chessRef.current.moves({ square: square as any, verbose: true });
      const newSquares: Record<string, React.CSSProperties> = {};
      
      // Highlight the selected square
      newSquares[square] = { backgroundColor: 'rgba(255, 255, 0, 0.4)' };
      
      // Highlight legal move destinations
      moves.forEach((move: any) => {
        newSquares[move.to] = {
          background: move.captured
            ? 'radial-gradient(circle, rgba(0,0,0,.1) 85%, transparent 85%)'
            : 'radial-gradient(circle, rgba(0,0,0,.1) 25%, transparent 25%)',
          borderRadius: '50%',
        };
      });
      
      setOptionSquares(newSquares);
    }
  }, [selectedSquare, onMove]);

  const onPieceDragBegin = useCallback(({ square }: any) => {
    if (square) {
      setSelectedSquare(square);
      
      // Get legal moves for the selected square
      const moves = chessRef.current.moves({ square: square as any, verbose: true });
      const newSquares: Record<string, React.CSSProperties> = {};
      
      // Highlight the selected square
      newSquares[square] = { backgroundColor: 'rgba(255, 255, 0, 0.4)' };
      
      // Highlight legal move destinations
      moves.forEach((move: any) => {
        newSquares[move.to] = {
          background: move.captured
            ? 'radial-gradient(circle, rgba(0,0,0,.1) 85%, transparent 85%)'
            : 'radial-gradient(circle, rgba(0,0,0,.1) 25%, transparent 25%)',
          borderRadius: '50%',
        };
      });
      
      setOptionSquares(newSquares);
    }
  }, []);

  const onPieceDrop = useCallback(({ sourceSquare, targetSquare }: any): boolean => {
    if (!targetSquare) return false;
    const success = onMove(sourceSquare, targetSquare);
    setSelectedSquare(null);
    setOptionSquares({});
    return success;
  }, [onMove]);

  const boardStyle = useMemo(() => ({
    borderRadius: '4px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  }), []);

  const darkSquareStyle = useMemo(() => ({ backgroundColor: '#b58863' }), []);
  const lightSquareStyle = useMemo(() => ({ backgroundColor: '#f0d9b5' }), []);

  // Combine last move highlighting with selection highlighting
  const customSquareStyles = useMemo(() => {
    const styles: Record<string, React.CSSProperties> = { ...optionSquares };
    
    // Add last move highlighting (dull yellow like chess.com)
    if (lastMove) {
      styles[lastMove.from] = {
        ...styles[lastMove.from],
        backgroundColor: 'rgba(205, 210, 106, 0.5)', // Dull yellow
      };
      styles[lastMove.to] = {
        ...styles[lastMove.to],
        backgroundColor: 'rgba(205, 210, 106, 0.5)', // Dull yellow
      };
    }
    
    return styles;
  }, [optionSquares, lastMove]);

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
        gap: '0.25rem',
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
        key={fen}
        options={{
          position: fen,
          boardOrientation: boardOrientation,
          squareStyles: customSquareStyles,
          boardStyle: {
            ...boardStyle,
            width: '700px',
            height: '700px',
          },
          darkSquareStyle: darkSquareStyle,
          lightSquareStyle: lightSquareStyle,
          animationDurationInMs: 100,
          allowDragging: !isViewingHistory,
          onSquareClick: isViewingHistory ? undefined : onSquareClick,
          onPieceDrag: isViewingHistory ? undefined : onPieceDragBegin,
          onPieceDrop: isViewingHistory ? undefined : onPieceDrop,
        }}
      />
    </div>
  );
};
