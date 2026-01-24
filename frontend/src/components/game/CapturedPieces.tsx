import React from 'react';

interface CapturedPiecesProps {
    pieces: string[]; // 'p', 'n', 'b', 'r', 'q'
    pieceColor: 'white' | 'black';
    score?: number;
}

const PIECE_ICONS: Record<string, Record<string, string>> = {
    white: {
        p: '♙', n: '♘', b: '♗', r: '♖', q: '♕', k: '♔'
    },
    black: {
        p: '♟', n: '♞', b: '♝', r: '♜', q: '♛', k: '♚'
    }
};

const PIECE_VALUES: Record<string, number> = {
    p: 1, n: 3, b: 3, r: 5, q: 9
};

export const CapturedPieces: React.FC<CapturedPiecesProps> = ({ pieces, pieceColor, score }) => {
    const sortedPieces = [...pieces].sort((a, b) => PIECE_VALUES[a] - PIECE_VALUES[b]);

    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem',
            padding: '2px 0',
            minHeight: '1.2rem',
            boxSizing: 'border-box',
        }}>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1px' }}>
                {sortedPieces.map((p, i) => (
                    <span
                        key={i}
                        style={{
                            fontSize: '1.1rem',
                            lineHeight: '1',
                            color: pieceColor === 'white' ? '#fff' : '#000',
                            textShadow: pieceColor === 'black' ? '0 0 1px rgba(255,255,255,0.8)' : '0 0 1px rgba(0,0,0,0.5)',
                            cursor: 'default',
                        }}
                        title={p}
                    >
                        {PIECE_ICONS[pieceColor][p]}
                    </span>
                ))}
            </div>
            {score !== undefined && score > 0 && (
                <span style={{
                    fontSize: '0.8rem',
                    fontWeight: 'bold',
                    color: '#888',
                    marginLeft: '4px',
                }}>
                    +{score}
                </span>
            )}
        </div>
    );
};
