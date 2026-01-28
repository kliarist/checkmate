import { useEffect, useState } from 'react';
import { joinMatchmakingQueue, leaveMatchmakingQueue } from '../../api/matchmakingApi';
import { useAuth } from '../../context/AuthContext';
import { useGameWebSocket } from '../../hooks/useGameWebSocket';
import './MatchmakingModal.css';

interface MatchmakingModalProps {
  timeControl: string;
  onCancel: () => void;
  onGameFound: (gameId: string) => void;
}

/**
 * MatchmakingModal - Shows searching animation and handles matchmaking
 */
const MatchmakingModal = ({ timeControl, onCancel, onGameFound }: MatchmakingModalProps) => {
  const [elapsedTime, setElapsedTime] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  // Subscribe to matchmaking events
  useGameWebSocket({
    userId: user?.id,
    onGameFound: (game) => {
      console.log('Game found:', game);
      onGameFound(game.gameId);
    },
  });

  useEffect(() => {
    if (!user) return;

    // Join matchmaking queue
    const joinQueue = async () => {
      try {
        await joinMatchmakingQueue(user.id, timeControl);
      } catch (err) {
        setError('Failed to join matchmaking queue');
        console.error('Matchmaking error:', err);
      }
    };

    joinQueue();

    // Start timer
    const timer = setInterval(() => {
      setElapsedTime((prev) => prev + 1);
    }, 1000);

    // Timeout after 5 minutes
    const timeout = setTimeout(() => {
      setError('Matchmaking timeout. No opponent found.');
      handleCancel();
    }, 300000); // 5 minutes

    return () => {
      clearInterval(timer);
      clearTimeout(timeout);
    };
  }, [user, timeControl]);

  const handleCancel = async () => {
    if (user) {
      try {
        await leaveMatchmakingQueue(user.id);
      } catch (err) {
        console.error('Error leaving queue:', err);
      }
    }
    onCancel();
  };

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="matchmaking-modal-overlay">
      <div className="matchmaking-modal">
        <div className="matchmaking-content">
          <h2>Finding Opponent</h2>
          
          <div className="searching-animation">
            <div className="spinner"></div>
          </div>

          <div className="matchmaking-info">
            <p className="time-control-info">
              Time Control: <strong>{timeControl}</strong>
            </p>
            <p className="elapsed-time">
              Searching for {formatTime(elapsedTime)}
            </p>
            {user?.eloRating && (
              <p className="rating-info">
                Your Rating: <strong>{user.eloRating}</strong>
              </p>
            )}
          </div>

          {error && (
            <div className="error-message" role="alert">
              {error}
            </div>
          )}

          <button 
            className="cancel-button"
            onClick={handleCancel}
            aria-label="Cancel matchmaking"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default MatchmakingModal;
