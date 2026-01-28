import { useEffect, useState } from 'react';
import './ChessClock.css';

interface ChessClockProps {
  whiteTimeMs: number;
  blackTimeMs: number;
  currentTurn: 'white' | 'black';
  incrementMs?: number;
  playerColor: 'white' | 'black';
}

/**
 * ChessClock - Displays chess clock for both players
 * Shows time remaining with visual indicators for time pressure
 */
const ChessClock = ({ 
  whiteTimeMs, 
  blackTimeMs, 
  currentTurn, 
  incrementMs = 0,
}: ChessClockProps) => {
  const [displayWhiteTime, setDisplayWhiteTime] = useState(whiteTimeMs);
  const [displayBlackTime, setDisplayBlackTime] = useState(blackTimeMs);

  useEffect(() => {
    setDisplayWhiteTime(whiteTimeMs);
    setDisplayBlackTime(blackTimeMs);
  }, [whiteTimeMs, blackTimeMs]);

  const formatTime = (ms: number): string => {
    const totalSeconds = Math.max(0, Math.floor(ms / 1000));
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  const isLowTime = (ms: number): boolean => {
    return ms < 10000; // Less than 10 seconds
  };

  const isActive = (color: 'white' | 'black'): boolean => {
    return currentTurn === color;
  };

  return (
    <div className="chess-clock">
      <div 
        className={`clock-display ${isActive('black') ? 'active' : ''} ${isLowTime(displayBlackTime) ? 'low-time' : ''}`}
        aria-label={`Black player time: ${formatTime(displayBlackTime)}`}
      >
        <div className="clock-label">Black</div>
        <div className="clock-time">{formatTime(displayBlackTime)}</div>
        {incrementMs > 0 && (
          <div className="clock-increment">+{incrementMs / 1000}s</div>
        )}
      </div>

      <div 
        className={`clock-display ${isActive('white') ? 'active' : ''} ${isLowTime(displayWhiteTime) ? 'low-time' : ''}`}
        aria-label={`White player time: ${formatTime(displayWhiteTime)}`}
      >
        <div className="clock-label">White</div>
        <div className="clock-time">{formatTime(displayWhiteTime)}</div>
        {incrementMs > 0 && (
          <div className="clock-increment">+{incrementMs / 1000}s</div>
        )}
      </div>
    </div>
  );
};

export default ChessClock;
