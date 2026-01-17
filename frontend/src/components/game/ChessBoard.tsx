import { Chessboard } from 'react-chessboard';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => void;
}

export const ChessBoard = ({ fen, onMove }: ChessBoardProps) => {
  const handleDrop = (sourceSquare: string, targetSquare: string) => {
    onMove(sourceSquare, targetSquare);
    return true;
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <Chessboard position={fen} onPieceDrop={handleDrop} />
    </div>
  );
};

