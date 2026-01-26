import { useState, useEffect, useRef, useCallback } from 'react';
import { Chess } from 'chess.js';
import apiClient from '../api/client';
import { useWebSocket } from '../context/WebSocketContext';
import { soundManager } from '../utils/soundManager';

export const useChessGame = (gameId: string) => {
  // Chess instance for move validation - use ref to maintain instance across renders
  const chessRef = useRef(new Chess());

  const [fen, setFen] = useState(chessRef.current.fen());
  const [moves, setMoves] = useState<any[]>([]);
  const [moveHistory, setMoveHistory] = useState<string[]>([chessRef.current.fen()]);
  const [currentMoveIndex, setCurrentMoveIndex] = useState(-1);
  const [isGameOver, setIsGameOver] = useState(false);
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [connectionError, setConnectionError] = useState(false);
  const [playerColor, setPlayerColor] = useState<'white' | 'black'>('white');
  const [capturedByWhite, setCapturedByWhite] = useState<string[]>([]);
  const [capturedByBlack, setCapturedByBlack] = useState<string[]>([]);
  const [materialScore, setMaterialScore] = useState(0);
  const [lastMove, setLastMove] = useState<{ from: string; to: string } | null>(null);
  const [moveDetails, setMoveDetails] = useState<Array<{ from: string; to: string; notation: string }>>([]);

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
      
      // Try to extract move details from the message or by comparing positions
      const moveFrom = message.from;
      const moveTo = message.to;
      
      // If not provided in message, we'll need to parse the algebraic notation
      // For now, we'll store what we have
      setMoves(prevMoves => [...prevMoves, {
        notation: moveNotation,
        number: chessRef.current.moveNumber()
      }]);

      // Track last move from WebSocket message
      if (moveFrom && moveTo) {
        setLastMove({ from: moveFrom, to: moveTo });
        setMoveDetails(prevDetails => [...prevDetails, { from: moveFrom, to: moveTo, notation: moveNotation }]);
      }

      announceToScreenReader(`Opponent played ${moveNotation}`);

      if (message.isCheckmate) {
        setIsGameOver(true);
        setResult('Checkmate!');
        soundManager.play('checkmate');
        announceToScreenReader('Checkmate! Game over.');
      } else if (message.isStalemate) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
        announceToScreenReader('Stalemate. Game is a draw.');
      } else if (chessRef.current.inCheck()) {
        // Only play check sound, not the move sound
        soundManager.play('check');
      } else {
        // Play sound for opponent move (only if not check)
        if (moveNotation === 'O-O' || moveNotation === 'O-O-O') {
          soundManager.play('castle');
        } else if (moveNotation.includes('=')) {
          // Promotion (e.g., e8=Q)
          soundManager.play('promote');
        } else if (message.captured || moveNotation.includes('x')) {
          soundManager.play('capture');
        } else {
          soundManager.play('move');
        }
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

      try {
        const movesResponse = await apiClient.get(`/api/games/${gameId}/moves`);
        const movesData = movesResponse.data.data;
        const moveList = movesData.map((move: any) => ({
          notation: move.algebraicNotation,
          number: move.moveNumber
        }));
        setMoves(moveList);

        // Reset chess instance and replay all moves to rebuild history
        chessRef.current.reset();
        const fenHistory = [chessRef.current.fen()];
        const detailedMoves: Array<{ from: string; to: string; notation: string }> = [];

        moveList.forEach((move: any) => {
          try {
            const moveResult = chessRef.current.move(move.notation);
            if (moveResult) {
              detailedMoves.push({
                from: moveResult.from,
                to: moveResult.to,
                notation: moveResult.san
              });
            }
            fenHistory.push(chessRef.current.fen());
          } catch (e) {
            console.error('Failed to replay move:', move.notation, e);
          }
        });

        // Now chessRef.current has the full history and captured pieces
        setFen(chessRef.current.fen());
        setMoveHistory(fenHistory);
        setMoveDetails(detailedMoves);
        setCurrentMoveIndex(moveList.length - 1);
      } catch (movesErr) {
        console.error('[useChessGame] Failed to load moves:', movesErr);
        // Fallback: just load the FEN without history
        chessRef.current.load(loadedFen);
        setFen(loadedFen);
      }

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
      } catch {
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
      setMoveHistory(prevHistory => [...prevHistory, newFen]);
      setMoveDetails(prevDetails => [...prevDetails, { from: move.from, to: move.to, notation: move.san }]);
      setCurrentMoveIndex(prev => prev + 1);
      setLastMove({ from: move.from, to: move.to });
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
        soundManager.play('checkmate');
        announceToScreenReader('Checkmate! You won the game.');
      } else if (chess.isStalemate()) {
        setIsGameOver(true);
        setResult('Stalemate - Draw');
        announceToScreenReader('Stalemate. Game is a draw.');
      } else if (chess.inCheck()) {
        // Only play check sound, not the move sound
        soundManager.play('check');
      } else {
        // Play sound based on move type (only if not check)
        if (move.flags.includes('k') || move.flags.includes('q')) {
          // Castling move (kingside or queenside)
          soundManager.play('castle');
        } else if (move.flags.includes('p')) {
          // Promotion
          soundManager.play('promote');
        } else if (move.captured) {
          soundManager.play('capture');
        } else {
          soundManager.play('move');
        }
      }

      return true;
    } catch (err: any) {
      console.error('[useChessGame] Invalid move exception:', err);
      return false;
    }
  }, [gameId, announceToScreenReader]);

  // Calculate captured pieces and score based on current position in move history
  useEffect(() => {
    // Create a temporary chess instance to replay moves up to currentMoveIndex
    const tempChess = new Chess();
    const whiteCaptures: string[] = [];
    const blackCaptures: string[] = [];

    // Replay moves up to the current index
    for (let i = 0; i <= currentMoveIndex && i < moves.length; i++) {
      try {
        const move = tempChess.move(moves[i].notation);
        if (move && move.captured) {
          if (move.color === 'w') {
            whiteCaptures.push(move.captured);
          } else {
            blackCaptures.push(move.captured);
          }
        }
      } catch (e) {
        console.error('Failed to replay move for captures:', moves[i].notation, e);
      }
    }

    setCapturedByWhite(whiteCaptures);
    setCapturedByBlack(blackCaptures);

    const pieceValues: Record<string, number> = { p: 1, n: 3, b: 3, r: 5, q: 9 };
    const valueCapturedByWhite = whiteCaptures.reduce((sum, p) => sum + pieceValues[p], 0);
    const valueCapturedByBlack = blackCaptures.reduce((sum, p) => sum + pieceValues[p], 0);

    // Score = White Material Advantage
    // If White captured more value (meaning Black lost more), White has advantage.
    setMaterialScore(valueCapturedByWhite - valueCapturedByBlack);
  }, [currentMoveIndex, moves]);

  // Update lastMove based on currentMoveIndex for historical navigation
  useEffect(() => {
    if (currentMoveIndex >= 0 && currentMoveIndex < moveDetails.length) {
      const move = moveDetails[currentMoveIndex];
      setLastMove({ from: move.from, to: move.to });
    } else {
      setLastMove(null);
    }
  }, [currentMoveIndex, moveDetails]);

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

  const goToMove = useCallback((index: number) => {
    if (index >= -1 && index < moveHistory.length - 1) {
      setCurrentMoveIndex(index);
      const targetFen = moveHistory[index + 1];
      chessRef.current.load(targetFen);
      setFen(targetFen);

      // Play sound for the move we're navigating to
      if (index >= 0 && index < moves.length) {
        const moveNotation = moves[index].notation;
        if (moveNotation === 'O-O' || moveNotation === 'O-O-O') {
          soundManager.play('castle');
        } else if (moveNotation.includes('=')) {
          soundManager.play('promote');
        } else if (moveNotation.includes('x')) {
          soundManager.play('capture');
        } else {
          soundManager.play('move');
        }
      }
    }
  }, [moveHistory, moves]);

  const nextMove = useCallback(() => {
    if (currentMoveIndex < moveHistory.length - 2) {
      const newIndex = currentMoveIndex + 1;
      setCurrentMoveIndex(newIndex);
      const targetFen = moveHistory[newIndex + 1];
      chessRef.current.load(targetFen);
      setFen(targetFen);

      // Play sound for the move we're navigating to
      if (newIndex >= 0 && newIndex < moves.length) {
        const moveNotation = moves[newIndex].notation;
        if (moveNotation === 'O-O' || moveNotation === 'O-O-O') {
          soundManager.play('castle');
        } else if (moveNotation.includes('=')) {
          soundManager.play('promote');
        } else if (moveNotation.includes('x')) {
          soundManager.play('capture');
        } else {
          soundManager.play('move');
        }
      }
    }
  }, [currentMoveIndex, moveHistory, moves]);

  const previousMove = useCallback(() => {
    if (currentMoveIndex >= 0) {
      const newIndex = currentMoveIndex - 1;
      setCurrentMoveIndex(newIndex);
      const targetFen = moveHistory[newIndex + 1];
      chessRef.current.load(targetFen);
      setFen(targetFen);

      // Play sound for the move we're navigating to (or silence if going back to start)
      if (newIndex >= 0 && newIndex < moves.length) {
        const moveNotation = moves[newIndex].notation;
        if (moveNotation === 'O-O' || moveNotation === 'O-O-O') {
          soundManager.play('castle');
        } else if (moveNotation.includes('=')) {
          soundManager.play('promote');
        } else if (moveNotation.includes('x')) {
          soundManager.play('capture');
        } else {
          soundManager.play('move');
        }
      }
    }
  }, [currentMoveIndex, moveHistory, moves]);

  const resumeGame = useCallback(() => {
    const lastIndex = moveHistory.length - 2;
    if (currentMoveIndex !== lastIndex) {
      setCurrentMoveIndex(lastIndex);
      const targetFen = moveHistory[moveHistory.length - 1];
      chessRef.current.load(targetFen);
      setFen(targetFen);
    }
  }, [currentMoveIndex, moveHistory]);

  return {
    fen,
    moves,
    currentMoveIndex,
    isGameOver,
    result,
    makeMove,
    resign,
    goToMove,
    nextMove,
    previousMove,
    resumeGame,
    loading,
    error,
    connectionError,
    playerColor,
    capturedByWhite,
    capturedByBlack,
    materialScore,
    lastMove,
  };
};
