import React from 'react';

interface CapturedPiecesProps {
    pieces: string[]; // 'p', 'n', 'b', 'r', 'q'
    pieceColor: 'white' | 'black';
    score?: number;
}

const PIECE_ICONS: Record<string, Record<string, string>> = {
    white: {
        p: '♟', n: '♞', b: '♝', r: '♜', q: '♛', k: '♚'  // Use solid pieces for white
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
            minHeight: '2rem',
            height: '2rem',
            boxSizing: 'border-box',
        }}>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1px', minHeight: '1.5rem' }}>
                {sortedPieces.map((p, i) => (
                    <span
                        key={i}
                        style={{
                            fontSize: '1.5rem',
                            lineHeight: '1',
                            color: pieceColor === 'white' ? '#fff' : '#000',
                            filter: pieceColor === 'white' 
                                ? 'drop-shadow(0 0 1px rgba(0,0,0,0.5))' 
                                : 'drop-shadow(0 0 1px rgba(255,255,255,0.8))',
                            cursor: 'default',
                            fontWeight: '900',
                            WebkitFontSmoothing: 'antialiased',
                            MozOsxFontSmoothing: 'grayscale',
                            textRendering: 'optimizeLegibility',
                        }}
                        title={p}
                    >
                        {PIECE_ICONS[pieceColor][p]}
                    </span>
                ))}
            </div>
            <span style={{
                fontSize: '0.8rem',
                fontWeight: 'bold',
                color: '#888',
                marginLeft: '4px',
                minWidth: '2rem',
                visibility: (score !== undefined && score > 0) ? 'visible' : 'hidden',
            }}>
                +{score || 0}
            </span>
        </div>
    );
};
