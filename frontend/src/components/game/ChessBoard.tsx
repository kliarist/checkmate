import { useState } from 'react';
import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
}

export const ChessBoard = ({ fen, onMove }: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>('white');

  const handlePieceDrop = (sourceSquare: string, targetSquare: string): boolean => {
    return onMove(sourceSquare, targetSquare);
  };

  const flipBoard = () => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
  };

  return (
    <div style={{ width: '100%', maxWidth: '600px', margin: '0 auto' }}>
      <Chessboard
        key={fen}
        position={fen}
        onPieceDrop={handlePieceDrop}
        boardOrientation={boardOrientation}
        customBoardStyle={{
          borderRadius: '4px',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
        }}
        customDarkSquareStyle={{ backgroundColor: '#b58863' }}
        customLightSquareStyle={{ backgroundColor: '#f0d9b5' }}
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

