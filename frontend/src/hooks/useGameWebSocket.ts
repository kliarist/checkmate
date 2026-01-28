import { useEffect, useState } from 'react';
import { useWebSocket } from '../context/WebSocketContext';

interface ClockUpdate {
  whiteTimeMs: number;
  blackTimeMs: number;
  currentTurn: 'white' | 'black';
}

interface GameFoundEvent {
  gameId: string;
  whitePlayerId: string;
  blackPlayerId: string;
  timeControl: string;
}

interface UseGameWebSocketProps {
  gameId?: string;
  userId?: string;
  onMove?: (move: any) => void;
  onClockUpdate?: (clock: ClockUpdate) => void;
  onGameFound?: (game: GameFoundEvent) => void;
  onTimeout?: (winner: string) => void;
}

/**
 * Custom hook for game-related WebSocket subscriptions
 * Handles moves, clock updates, matchmaking, and game events
 */
export const useGameWebSocket = ({
  gameId,
  userId,
  onMove,
  onClockUpdate,
  onGameFound,
  onTimeout,
}: UseGameWebSocketProps) => {
  const { subscribe, isConnected } = useWebSocket();
  const [clockState, setClockState] = useState<ClockUpdate | null>(null);

  // Subscribe to game moves
  useEffect(() => {
    if (!gameId || !isConnected) return;

    const unsubscribe = subscribe(`/topic/game/${gameId}/moves`, (message) => {
      console.log('[WebSocket] Move received:', message);
      if (onMove) {
        onMove(message);
      }
    });

    return unsubscribe;
  }, [gameId, isConnected, subscribe, onMove]);

  // Subscribe to clock updates
  useEffect(() => {
    if (!gameId || !isConnected) return;

    const unsubscribe = subscribe(`/topic/game/${gameId}/clock`, (message: ClockUpdate) => {
      console.log('[WebSocket] Clock update:', message);
      setClockState(message);
      if (onClockUpdate) {
        onClockUpdate(message);
      }

      // Check for timeout
      if (message.whiteTimeMs <= 0 || message.blackTimeMs <= 0) {
        const winner = message.whiteTimeMs <= 0 ? 'black' : 'white';
        if (onTimeout) {
          onTimeout(winner);
        }
      }
    });

    return unsubscribe;
  }, [gameId, isConnected, subscribe, onClockUpdate, onTimeout]);

  // Subscribe to matchmaking events
  useEffect(() => {
    if (!userId || !isConnected) return;

    const unsubscribe = subscribe(`/user/${userId}/queue`, (message: GameFoundEvent) => {
      console.log('[WebSocket] Game found:', message);
      if (onGameFound) {
        onGameFound(message);
      }
    });

    return unsubscribe;
  }, [userId, isConnected, subscribe, onGameFound]);

  return {
    isConnected,
    clockState,
  };
};
