import { useState, useEffect, useMemo } from 'react';
import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
  playerColor?: 'white' | 'black';
  onFlipBoard?: () => void;
  onResign?: () => void;
  onOfferDraw?: () => void;
}

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

  const handleFlipBoard = () => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
    onFlipBoard?.();
  };

  // Memoize board styles to prevent unnecessary re-renders
  const boardStyle = useMemo(() => ({
    borderRadius: '4px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
  }), []);

  const darkSquareStyle = useMemo(() => ({ backgroundColor: '#b58863' }), []);
  const lightSquareStyle = useMemo(() => ({ backgroundColor: '#f0d9b5' }), []);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: '1rem',
    }}>
      <Chessboard
        id="BasicBoard"
        boardWidth={600}
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

      {/* Action buttons below the board */}
      <div style={{
        display: 'flex',
        gap: '0.5rem',
        justifyContent: 'center',
      }}>
        <button
          onClick={handleFlipBoard}
          title="Flip Board"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#3a3a3a',
            color: '#e0e0e0',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          ⇅
        </button>

        <button
          onClick={onOfferDraw}
          title="Offer Draw"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#3a3a3a',
            color: '#e0e0e0',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          🤝
        </button>

        <button
          onClick={onResign}
          title="Resign"
          style={{
            padding: '0.5rem',
            width: '36px',
            height: '36px',
            backgroundColor: '#f44336',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          🏳
        </button>
      </div>
    </div>
  );
};
