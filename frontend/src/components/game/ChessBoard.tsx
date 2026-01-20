import { useState, useRef } from 'react';
import { Chess } from 'chess.js';

type Square = 'a8' | 'b8' | 'c8' | 'd8' | 'e8' | 'f8' | 'g8' | 'h8' |
              'a7' | 'b7' | 'c7' | 'd7' | 'e7' | 'f7' | 'g7' | 'h7' |
              'a6' | 'b6' | 'c6' | 'd6' | 'e6' | 'f6' | 'g6' | 'h6' |
              'a5' | 'b5' | 'c5' | 'd5' | 'e5' | 'f5' | 'g5' | 'h5' |
              'a4' | 'b4' | 'c4' | 'd4' | 'e4' | 'f4' | 'g4' | 'h4' |
              'a3' | 'b3' | 'c3' | 'd3' | 'e3' | 'f3' | 'g3' | 'h3' |
              'a2' | 'b2' | 'c2' | 'd2' | 'e2' | 'f2' | 'g2' | 'h2' |
              'a1' | 'b1' | 'c1' | 'd1' | 'e1' | 'f1' | 'g1' | 'h1';

interface ChessBoardProps {
  fen: string;
  onMove: (from: string, to: string) => boolean;
}

const SQUARES: Square[] = [
  'a8', 'b8', 'c8', 'd8', 'e8', 'f8', 'g8', 'h8',
  'a7', 'b7', 'c7', 'd7', 'e7', 'f7', 'g7', 'h7',
  'a6', 'b6', 'c6', 'd6', 'e6', 'f6', 'g6', 'h6',
  'a5', 'b5', 'c5', 'd5', 'e5', 'f5', 'g5', 'h5',
  'a4', 'b4', 'c4', 'd4', 'e4', 'f4', 'g4', 'h4',
  'a3', 'b3', 'c3', 'd3', 'e3', 'f3', 'g3', 'h3',
  'a2', 'b2', 'c2', 'd2', 'e2', 'f2', 'g2', 'h2',
  'a1', 'b1', 'c1', 'd1', 'e1', 'f1', 'g1', 'h1',
];

const PIECE_SYMBOLS: Record<string, string> = {
  'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
  'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟',
};

export const ChessBoard = ({ fen, onMove }: ChessBoardProps) => {
  const [boardOrientation, setBoardOrientation] = useState<'white' | 'black'>('white');
  const [selectedSquare, setSelectedSquare] = useState<Square | null>(null);
  const [draggedPiece, setDraggedPiece] = useState<{ square: Square; piece: string } | null>(null);
  const boardRef = useRef<HTMLDivElement>(null);

  const chess = new Chess(fen);

  const handleSquareClick = (square: Square) => {
    if (selectedSquare) {
      // Try to make a move
      onMove(selectedSquare, square);
      setSelectedSquare(null);
    } else {
      // Select a piece
      const piece = chess.get(square);
      if (piece) {
        setSelectedSquare(square);
      }
    }
  };

  const handleDragStart = (e: React.DragEvent, square: Square) => {
    const piece = chess.get(square);
    if (piece) {
      setDraggedPiece({ square, piece: piece.type });
      e.dataTransfer.effectAllowed = 'move';
      e.dataTransfer.setData('text/plain', square);
      // Create a transparent drag image to use default piece visibility
      const dragImage = new Image();
      dragImage.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
      e.dataTransfer.setDragImage(dragImage, 0, 0);
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDrop = (e: React.DragEvent, targetSquare: Square) => {
    e.preventDefault();
    if (draggedPiece) {
      onMove(draggedPiece.square, targetSquare);
      setDraggedPiece(null);
    }
  };

  const handleDragEnd = () => {
    setDraggedPiece(null);
  };

  const flipBoard = () => {
    setBoardOrientation((prev) => (prev === 'white' ? 'black' : 'white'));
  };

  const renderSquare = (square: Square, rowIndex: number, colIndex: number) => {
    const isLight = (rowIndex + colIndex) % 2 === 0;
    const piece = chess.get(square);
    const isSelected = selectedSquare === square;
    const isDragging = draggedPiece?.square === square;

    return (
      <div
        key={square}
        onClick={() => handleSquareClick(square)}
        onDragOver={handleDragOver}
        onDrop={(e) => handleDrop(e, square)}
        style={{
          width: '12.5%',
          aspectRatio: '1 / 1',
          backgroundColor: isLight ? '#f0d9b5' : '#b58863',
          border: isSelected ? '3px solid #646cff' : 'none',
          boxSizing: 'border-box',
          cursor: piece ? 'pointer' : 'default',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
        }}
      >
        {piece && (
          <div
            draggable={true}
            onDragStart={(e) => handleDragStart(e, square)}
            onDragEnd={handleDragEnd}
            style={{
              fontSize: '60px',
              cursor: isDragging ? 'grabbing' : 'grab',
              opacity: isDragging ? 0.5 : 1,
              color: piece.color === 'w' ? '#f0f0f0' : '#333',
              textShadow: piece.color === 'w'
                ? '1px 1px 2px #000, 0 0 1px #000'
                : '1px 1px 2px #fff, 0 0 1px #fff',
              userSelect: 'none',
              lineHeight: 1,
            }}
          >
            {PIECE_SYMBOLS[piece.color === 'w' ? piece.type.toUpperCase() : piece.type.toLowerCase()]}
          </div>
        )}
        <div
          style={{
            position: 'absolute',
            bottom: '2px',
            left: '2px',
            fontSize: '10px',
            fontWeight: 'bold',
            color: isLight ? '#b58863' : '#f0d9b5',
            userSelect: 'none',
          }}
        >
          {colIndex === 0 && String(8 - rowIndex)}
          {rowIndex === 7 && square[0]}
        </div>
      </div>
    );
  };

  const squares = boardOrientation === 'white' ? SQUARES : [...SQUARES].reverse();

  return (
    <div style={{ width: '100%', maxWidth: '600px', margin: '0 auto' }}>
      <div
        ref={boardRef}
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          width: '100%',
          aspectRatio: '1 / 1',
          border: '2px solid #333',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
          boxSizing: 'border-box',
        }}
      >
        {squares.map((square, index) => {
          const rowIndex = Math.floor(index / 8);
          const colIndex = index % 8;
          return renderSquare(square, rowIndex, colIndex);
        })}
      </div>
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

