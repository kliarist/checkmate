import { useState, useRef, useCallback } from 'react';
import { Chess } from 'chess.js';
import { ChessBoard } from './ChessBoard';

export const TestBoard = () => {
    const chessRef = useRef(new Chess());
    const [fen, setFen] = useState(chessRef.current.fen());

    const makeMove = useCallback((from: string, to: string): boolean => {
        console.log('[TestBoard] makeMove called:', { from, to });
        const chess = chessRef.current;

        try {
            const move = chess.move({
                from,
                to,
                promotion: 'q',
            });

            if (move === null) {
                console.log('[TestBoard] Invalid move');
                return false;
            }

            console.log('[TestBoard] Valid move:', move.san);
            const newFen = chess.fen();
            console.log('[TestBoard] New FEN:', newFen);
            setFen(newFen);
            return true;
        } catch (e) {
            console.error('[TestBoard] Move error:', e);
            return false;
        }
    }, []);

    const reset = useCallback(() => {
        chessRef.current = new Chess();
        setFen(chessRef.current.fen());
    }, []);

    return (
        <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
            <h2>Test Board (using ChessBoard component)</h2>
            <p>This tests the same ChessBoard component used in GamePage</p>
            <ChessBoard fen={fen} onMove={makeMove} playerColor="white" />
            <button onClick={reset} style={{ marginTop: '1rem', padding: '0.5rem 1rem' }}>
                Reset
            </button>
        </div>
    );
};
