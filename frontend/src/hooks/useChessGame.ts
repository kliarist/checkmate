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
  const { subscribe, send, isConnected } = useWebSocket();

  useEffect(() => {
    loadGame();
  }, [gameId]);

  useEffect(() => {
    if (!isConnected) return;

    const unsubscribe = subscribe(`/topic/game/${gameId}/moves`, (message) => {
      handleOpponentMove(message);
    });

    return unsubscribe;
  }, [gameId, isConnected]);

  const loadGame = async () => {
    try {
      const response = await apiClient.get(`/api/games/${gameId}`);
      const game = response.data.data;
      chess.load(game.currentFen);
      setFen(game.currentFen);
      setLoading(false);
    } catch (error) {
      console.error('Failed to load game:', error);
      setLoading(false);
    }
  };

  const makeMove = (from: string, to: string) => {
    try {
      const move = chess.move({ from, to });
      if (!move) return;

      setFen(chess.fen());
      setMoves([...moves, { notation: move.san, number: chess.moveNumber() }]);

      send(`/app/game/${gameId}/move`, { from, to, promotion: null });

      if (chess.isCheckmate()) {
        setIsGameOver(true);
        setResult('Checkmate!');
      } else if (chess.isStalemate()) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
      }
    } catch (error) {
      console.error('Invalid move:', error);
    }
  };

  const handleOpponentMove = (message: any) => {
    chess.load(message.fen);
    setFen(message.fen);
    setMoves([...moves, { notation: message.algebraicNotation, number: chess.moveNumber() }]);

    if (message.isCheckmate) {
      setIsGameOver(true);
      setResult('Checkmate!');
    } else if (message.isStalemate) {
      setIsGameOver(true);
      setResult('Stalemate - Draw');
    }
  };

  const resign = async () => {
    try {
      await apiClient.post(`/api/games/${gameId}/resign`, null, {
        params: { playerId: 'guest' },
      });
      setIsGameOver(true);
      setResult('You resigned');
    } catch (error) {
      console.error('Failed to resign:', error);
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
  };
};

