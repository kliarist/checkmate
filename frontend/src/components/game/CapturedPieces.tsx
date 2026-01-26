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
    // Group pieces by type and count them
    const pieceCounts: Record<string, number> = {};
    pieces.forEach(p => {
        pieceCounts[p] = (pieceCounts[p] || 0) + 1;
    });

    // Sort by piece value
    const sortedPieceTypes = Object.keys(pieceCounts).sort((a, b) => PIECE_VALUES[a] - PIECE_VALUES[b]);

    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0',
            padding: '2px 0',
            minHeight: '2rem',
            height: '2rem',
            boxSizing: 'border-box',
        }}>
            <div style={{ display: 'flex', gap: '0', minHeight: '1.5rem', alignItems: 'center', marginLeft: '0' }}>
                {sortedPieceTypes.map((pieceType) => (
                    <div key={pieceType} style={{ 
                        position: 'relative',
                        display: 'inline-block',
                        marginLeft: '-5px',
                    }}>
                        {Array.from({ length: pieceCounts[pieceType] }).map((_, index) => (
                            <span
                                key={index}
                                style={{
                                    position: index === 0 ? 'relative' : 'absolute',
                                    left: index === 0 ? '0' : `${index * 5}px`,
                                    top: '0',
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
                                title={`${pieceCounts[pieceType]} ${pieceType}`}
                            >
                                {PIECE_ICONS[pieceColor][pieceType]}
                            </span>
                        ))}
                        {/* Spacer to reserve space for stacked pieces */}
                        <span style={{ 
                            visibility: 'hidden',
                            marginRight: pieceCounts[pieceType] > 1 ? `${(pieceCounts[pieceType] - 1) * 5}px` : '0',
                            fontSize: '1.5rem',
                            lineHeight: '1',
                        }}>
                            {PIECE_ICONS[pieceColor][pieceType]}
                        </span>
                    </div>
                ))}
            </div>
            <span style={{
                fontSize: '0.8rem',
                fontWeight: 'bold',
                color: '#888',
                marginLeft: '0.5rem',
                minWidth: '2rem',
                visibility: (score !== undefined && score > 0) ? 'visible' : 'hidden',
            }}>
                +{score || 0}
            </span>
        </div>
    );
};
