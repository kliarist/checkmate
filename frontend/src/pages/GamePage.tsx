import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ChessBoard } from '../components/game/ChessBoard';
import { MoveList } from '../components/game/MoveList';
import { GameEndModal } from '../components/game/GameEndModal';
import { useChessGame } from '../hooks/useChessGame';

export const GamePage = () => {
  const { id } = useParams<{ id: string }>();
  const {
    fen,
    moves,
    isGameOver,
    result,
    makeMove,
    resign,
    loading,
  } = useChessGame(id!);

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '2rem' }}>Loading game...</div>;
  }

  return (
    <div style={{ display: 'flex', gap: '2rem', padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ flex: 1 }}>
        <ChessBoard fen={fen} onMove={makeMove} />
        <div style={{ marginTop: '1rem', textAlign: 'center' }}>
          <button
            onClick={resign}
            style={{
              padding: '0.5rem 2rem',
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
            }}
          >
            Resign
          </button>
        </div>
      </div>

      <div style={{ width: '300px' }}>
        <h3>Move History</h3>
        <MoveList moves={moves} />
      </div>

      {isGameOver && <GameEndModal result={result} onClose={() => window.location.href = '/'} />}
    </div>
  );
};

