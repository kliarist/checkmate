import { useState, useEffect, useMemo } from 'react';
import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
  playerColor?: 'white' | 'black';
}

export const ChessBoard = ({ fen, onMove, playerColor = 'white' }: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>(playerColor);
  const [selectedSquare, setSelectedSquare] = useState<string | null>(null);
  const [optionSquares, setOptionSquares] = useState<Record<string, React.CSSProperties>>({});

  // Update board orientation when playerColor prop changes
  useEffect(() => {
    setBoardOrientation(playerColor);
  }, [playerColor]);

  function onDrop(sourceSquare: string, targetSquare: string): boolean {
    const result = onMove(sourceSquare, targetSquare);
    setSelectedSquare(null);
    setOptionSquares({});
    return result;
  }

  function onSquareClick(square: string) {
    if (!selectedSquare) {
      setSelectedSquare(square);
      setOptionSquares({
        [square]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
      });
    } else {
      onMove(selectedSquare, square);
      setSelectedSquare(null);
      setOptionSquares({});
    }
  }

  function flipBoard() {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
  }

  // Memoize board styles to prevent unnecessary re-renders
  const boardStyle = useMemo(() => ({
    borderRadius: '4px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  }), []);

  const darkSquareStyle = useMemo(() => ({ backgroundColor: '#b58863' }), []);
  const lightSquareStyle = useMemo(() => ({ backgroundColor: '#f0d9b5' }), []);

  return (
    <div style={{
      width: '100%',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
    }}>
      <Chessboard
        id="BasicBoard"
        boardWidth={560}
        position={fen}
        boardOrientation={boardOrientation}
        onPieceDrop={onDrop}
        onSquareClick={onSquareClick}
        customSquareStyles={optionSquares}
        customBoardStyle={boardStyle}
        customDarkSquareStyle={darkSquareStyle}
        customLightSquareStyle={lightSquareStyle}
        animationDuration={200}
      />
      <div style={{ textAlign: 'center', marginTop: '1rem' }}>
        <button
          onClick={flipBoard}
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#2196F3',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1rem',
          }}
        >
          Flip Board
        </button>
      </div>
    </div>
  );
};
