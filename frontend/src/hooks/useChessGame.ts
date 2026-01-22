import { useState, useEffect, useRef, useCallback } from 'react';
import { Chess } from 'chess.js';
import apiClient from '../api/client';
import { useWebSocket } from '../context/WebSocketContext';

export const useChessGame = (gameId: string) => {
  // Chess instance for move validation - use ref to maintain instance across renders
  const chessRef = useRef(new Chess());

  // Game state
  const [fen, setFen] = useState(chessRef.current.fen());
  const [moves, setMoves] = useState<any[]>([]);
  const [isGameOver, setIsGameOver] = useState(false);
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [connectionError, setConnectionError] = useState(false);
  const [playerColor, setPlayerColor] = useState<'white' | 'black'>('white');

  // WebSocket
  const { subscribe, send, isConnected } = useWebSocket();

  // Refs for stable callbacks - prevents unnecessary re-renders and stale closures
  const sendRef = useRef(send);
  sendRef.current = send;
  const isConnectedRef = useRef(isConnected);
  isConnectedRef.current = isConnected;

  // Track pending moves we've sent to avoid processing our own WebSocket echoes
  const pendingMoveRef = useRef<string | null>(null);

  /**
   * Announce message to screen readers
   */
  const announceToScreenReader = useCallback((message: string) => {
    const announcer = document.getElementById('chess-announcer');
    if (announcer) {
      announcer.textContent = message;
    }
  }, []);

  /**
   * Handle moves received via WebSocket (from opponent or echoed back from our own move)
   */
  const handleWebSocketMove = useCallback((message: any) => {
    const moveNotation = message.algebraicNotation;

    if (pendingMoveRef.current && moveNotation === pendingMoveRef.current) {
      pendingMoveRef.current = null;
      if (message.fen && message.fen !== chessRef.current.fen()) {
        chessRef.current.load(message.fen);
        setFen(message.fen);
      }
      return;
    }

    try {
      chessRef.current.load(message.fen);
      setFen(message.fen);
      setMoves(prevMoves => [...prevMoves, {
        notation: moveNotation,
        number: chessRef.current.moveNumber()
      }]);

      announceToScreenReader(`Opponent played ${moveNotation}`);

      if (message.isCheckmate) {
        setIsGameOver(true);
        setResult('Checkmate!');
        announceToScreenReader('Checkmate! Game over.');
      } else if (message.isStalemate) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
        announceToScreenReader('Stalemate. Game is a draw.');
      }
    } catch (err) {
      console.error('[useChessGame] Failed to process move:', err);
      setError('Failed to process move. Game may be out of sync.');
    }
  }, [announceToScreenReader]);

  const loadGame = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await apiClient.get(`/api/games/${gameId}`);
      const game = response.data.data;
      const loadedFen = game.currentFen;

      chessRef.current.load(loadedFen);
      setFen(loadedFen);

      const guestUserId = localStorage.getItem('guestUserId');
      if (guestUserId) {
        if (game.whitePlayerId === guestUserId) {
          setPlayerColor('white');
        } else if (game.blackPlayerId === guestUserId) {
          setPlayerColor('black');
        }
      }
    } catch (err: any) {
      console.error('[useChessGame] Failed to load game:', err);
      chessRef.current.reset();
      setFen(chessRef.current.fen());
      setError('Failed to load game from server. Playing in offline mode.');
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  // Load game on mount
  useEffect(() => {
    loadGame();
  }, [loadGame]);

  // Subscribe to WebSocket for opponent moves
  useEffect(() => {
    if (!isConnected) {
      setConnectionError(true);
      return;
    }

    setConnectionError(false);
    return subscribe(`/topic/game/${gameId}/moves`, handleWebSocketMove);
  }, [gameId, isConnected, subscribe, handleWebSocketMove]);

  const makeMove = useCallback((from: string, to: string): boolean => {
    const chess = chessRef.current;

    try {
      let move = null;
      try {
        move = chess.move({ from, to });
      } catch (moveError: any) {
        try {
          move = chess.move({ from, to, promotion: 'q' });
        } catch (promotionError) {
          console.error('[useChessGame] Invalid move with promotion:', promotionError);
          return false;
        }
      }

      if (!move) {
        return false;
      }

      const newFen = chess.fen();
      setFen(newFen);
      setMoves(prevMoves => [...prevMoves, { notation: move.san, number: chess.moveNumber() }]);
      setError('');

      announceToScreenReader(`You played ${move.san}`);

      if (isConnectedRef.current) {
        try {
          pendingMoveRef.current = move.san;
          sendRef.current(`/app/game/${gameId}/move`, { from, to, promotion: move.promotion || null });
        } catch (e) {
          console.error('[useChessGame] Failed to send move:', e);
          pendingMoveRef.current = null;
        }
      }

      if (chess.isCheckmate()) {
        setIsGameOver(true);
        setResult('Checkmate!');
        announceToScreenReader('Checkmate! You won the game.');
      } else if (chess.isStalemate()) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
        announceToScreenReader('Stalemate. Game is a draw.');
      }

      return true;
    } catch (err: any) {
      console.error('[useChessGame] Invalid move exception:', err);
      return false;
    }
  }, [gameId, announceToScreenReader]);

  const resign = useCallback(async () => {
    try {
      const playerId = localStorage.getItem('guestUserId');

      if (!playerId) {
        setError('Player ID not found. Please refresh and try again.');
        setTimeout(() => setError(''), 3000);
        return;
      }

      await apiClient.post(`/api/games/${gameId}/resign`, null, {
        params: { playerId },
      });

      setIsGameOver(true);
      setResult('You resigned');
    } catch (err: any) {
      console.error('[useChessGame] Failed to resign:', err);
      setError(err.response?.data?.message || 'Failed to resign. Please try again.');
      setTimeout(() => setError(''), 3000);
    }
  }, [gameId]);

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
    playerColor,
  };
};
