import React from 'react';

interface PlayerInfoProps {
  username: string;
  eloRating: number;
  isComputer: boolean;
  isActive: boolean;
  color: 'white' | 'black';
  capturedPieces: string[];
  materialAdvantage: number;
}

export const PlayerInfo: React.FC<PlayerInfoProps> = ({
  username,
  eloRating,
  isComputer,
  isActive,
  color,
  capturedPieces,
  materialAdvantage,
}) => {
  // Debug logging
  console.log('[PlayerInfo]', { username, color, capturedPieces, materialAdvantage });
  
  // Generate avatar based on whether it's a computer or human
  const getAvatar = () => {
    if (isComputer) {
      if (eloRating <= 1000) return '🤖';
      else if (eloRating <= 1800) return '🦾';
      else return '🧠';
    }
    return color === 'white' ? '♔' : '♚';
  };

  // Get display name
  const getDisplayName = () => {
    if (isComputer) {
      if (eloRating <= 1000) return 'Stockfish (Beginner)';
      else if (eloRating <= 1800) return 'Stockfish (Intermediate)';
      else return 'Stockfish (Advanced)';
    }
    return username;
  };

  // Convert piece letters to Unicode symbols with correct color
  const getPieceSymbol = (piece: string, capturedColor: 'white' | 'black') => {
    const whiteSymbols: Record<string, string> = {
      'p': '♙', 'n': '♘', 'b': '♗', 'r': '♖', 'q': '♕', 'k': '♔',
    };
    const blackSymbols: Record<string, string> = {
      'p': '♟︎', 'n': '♞', 'b': '♝', 'r': '♜', 'q': '♛', 'k': '♚',
    };
    const symbols = capturedColor === 'white' ? whiteSymbols : blackSymbols;
    return symbols[piece.toLowerCase()] || piece;
  };
  
  // Determine the color of captured pieces (opposite of the player who captured them)
  const capturedPieceColor = color === 'white' ? 'black' : 'white';

  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '1rem',
        padding: '0.5rem 0',
        backgroundColor: 'transparent',
        borderRadius: '0',
        border: 'none',
        transition: 'all 0.2s ease',
      }}
    >
      {/* Avatar + Name + ELO */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
        <div
          style={{
            fontSize: '1.5rem',
            width: '36px',
            height: '36px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: 'transparent',
            borderRadius: '6px',
            flexShrink: 0,
          }}
        >
          {getAvatar()}
        </div>

        <div>
          <div
            style={{
              fontSize: '0.9rem',
              fontWeight: '600',
              color: '#ffffff',
              whiteSpace: 'nowrap',
            }}
          >
            {getDisplayName()}
          </div>
          <div
            style={{
              fontSize: '0.75rem',
              color: '#ffd700',
              fontWeight: '500',
            }}
          >
            {eloRating} ELO
          </div>
        </div>

        {isActive && (
          <div
            style={{
              width: '6px',
              height: '6px',
              backgroundColor: '#4caf50',
              borderRadius: '50%',
              animation: 'pulse 2s infinite',
              flexShrink: 0,
            }}
          />
        )}
      </div>

      {/* Captured Pieces */}
      {capturedPieces.length > 0 && (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.4rem',
          }}
        >
          <div
            style={{
              display: 'flex',
              gap: '0.15rem',
              fontSize: '1.1rem',
            }}
          >
            {capturedPieces.map((piece, index) => (
              <span key={index} style={{ opacity: 0.85 }}>
                {getPieceSymbol(piece, capturedPieceColor)}
              </span>
            ))}
          </div>
          {materialAdvantage > 0 && (
            <span
              style={{
                fontSize: '0.75rem',
                fontWeight: '700',
                color: '#4caf50',
              }}
            >
              +{materialAdvantage}
            </span>
          )}
        </div>
      )}

      <style>
        {`
          @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.4; }
          }
        `}
      </style>
    </div>
  );
};

