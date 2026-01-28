import { useState } from 'react';
import './TimeControlSelector.css';

interface TimeControl {
  id: string;
  name: string;
  time: string;
  increment: string;
  description: string;
}

const timeControls: TimeControl[] = [
  {
    id: 'bullet',
    name: 'Bullet',
    time: '1 min',
    increment: '+0s',
    description: 'Fast-paced games'
  },
  {
    id: 'blitz',
    name: 'Blitz',
    time: '5 min',
    increment: '+2s',
    description: 'Quick tactical games'
  },
  {
    id: 'rapid',
    name: 'Rapid',
    time: '10 min',
    increment: '+5s',
    description: 'Balanced gameplay'
  },
  {
    id: 'classical',
    name: 'Classical',
    time: '30 min',
    increment: '+30s',
    description: 'Deep strategic games'
  }
];

interface TimeControlSelectorProps {
  onSelect: (timeControl: string) => void;
  disabled?: boolean;
}

/**
 * TimeControlSelector - Displays time control options for ranked games
 */
const TimeControlSelector = ({ onSelect, disabled = false }: TimeControlSelectorProps) => {
  const [selected, setSelected] = useState<string | null>(null);

  const handleSelect = (timeControlId: string) => {
    if (disabled) return;
    setSelected(timeControlId);
    onSelect(timeControlId);
  };

  return (
    <div className="time-control-selector">
      {timeControls.map((tc) => (
        <button
          key={tc.id}
          className={`time-control-card ${selected === tc.id ? 'selected' : ''} ${disabled ? 'disabled' : ''}`}
          onClick={() => handleSelect(tc.id)}
          disabled={disabled}
          aria-label={`Select ${tc.name} time control: ${tc.time} ${tc.increment}`}
        >
          <div className="time-control-header">
            <h3>{tc.name}</h3>
            <div className="time-control-time">
              {tc.time} {tc.increment}
            </div>
          </div>
          <p className="time-control-description">{tc.description}</p>
        </button>
      ))}
    </div>
  );
};

export default TimeControlSelector;
