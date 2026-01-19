import { useState } from 'react';
import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => void;
}

export const ChessBoard = ({ fen, onMove }: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>('white');

  const handleDrop = (sourceSquare: string, targetSquare: string) => {
    onMove(sourceSquare, targetSquare);
    return true;
  };

  const flipBoard = () => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <Chessboard
        position={fen}
        onPieceDrop={handleDrop}
        boardOrientation={boardOrientation}
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
          }}
        >
          Flip Board
        </button>
      </div>
    </div>
  );
};

