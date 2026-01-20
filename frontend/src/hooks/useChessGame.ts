import { useState, useEffect } from 'react';
import { Chess } from 'chess.js';
import apiClient from '../api/client';
import { useWebSocket } from '../context/WebSocketContext';

export const useChessGame = (gameId: string) => {
  const [chess] = useState(new Chess());
  const [fen, setFen] = useState(chess.fen());
  const [moves, setMoves] = useState<any[]>([]);
  const [isGameOver, setIsGameOver] = useState(false);
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [connectionError, setConnectionError] = useState(false);
  const { subscribe, send, isConnected } = useWebSocket();

  useEffect(() => {
    loadGame();
  }, [gameId]);

  useEffect(() => {
    if (!isConnected) {
      setConnectionError(true);
      return;
    }

    setConnectionError(false);
    const unsubscribe = subscribe(`/topic/game/${gameId}/moves`, (message) => {
      handleOpponentMove(message);
    });

    return unsubscribe;
  }, [gameId, isConnected]);

  const loadGame = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await apiClient.get(`/api/games/${gameId}`);
      const game = response.data.data;
      chess.load(game.currentFen);
      setFen(game.currentFen);
    } catch (err: any) {
      console.error('Failed to load game:', err);
      setError('Failed to load game. Please refresh the page.');
    } finally {
      setLoading(false);
    }
  };

  const makeMove = (from: string, to: string): boolean => {
    try {
      const move = chess.move({ from, to });
      if (!move) {
        setError('Invalid move. Please try again.');
        setTimeout(() => setError(''), 3000);
        return false;
      }

      setFen(chess.fen());
      setMoves([...moves, { notation: move.san, number: chess.moveNumber() }]);
      setError('');

      if (!isConnected) {
        setError('Connection lost. Reconnecting...');
        chess.undo(); // Undo the move if not connected
        return false;
      }

      send(`/app/game/${gameId}/move`, { from, to, promotion: null });

      if (chess.isCheckmate()) {
        setIsGameOver(true);
        setResult('Checkmate!');
      } else if (chess.isStalemate()) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
      } else if (chess.isCheck()) {
        setError('Check!');
        setTimeout(() => setError(''), 2000);
      }

      return true;
    } catch (err: any) {
      console.error('Invalid move:', err);
      setError('Invalid move. Please try a different move.');
      setTimeout(() => setError(''), 3000);
      return false;
    }
  };

  const handleOpponentMove = (message: any) => {
    try {
      chess.load(message.fen);
      setFen(message.fen);
      setMoves([...moves, { notation: message.algebraicNotation, number: chess.moveNumber() }]);

      if (message.isCheckmate) {
        setIsGameOver(true);
        setResult('Checkmate!');
      } else if (message.isStalemate) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
      } else if (message.isCheck) {
        setError('Check!');
        setTimeout(() => setError(''), 2000);
      }
    } catch (err) {
      console.error('Failed to process opponent move:', err);
      setError('Failed to process move. Game may be out of sync.');
    }
  };

  const resign = async () => {
    try {
      await apiClient.post(`/api/games/${gameId}/resign`, null, {
        params: { playerId: 'guest' },
      });
      setIsGameOver(true);
      setResult('You resigned');
    } catch (err: any) {
      console.error('Failed to resign:', err);
      setError('Failed to resign. Please try again.');
    }
  };

  return {
    fen,
    moves,
    isGameOver,
    result,
    makeMove,
    resign,
    loading,
    error,
    connectionError,
  };
};

