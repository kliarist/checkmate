import React from 'react';

interface GameControlsProps {
  onFlipBoard?: () => void;
  onOfferDraw?: () => void;
  onResign?: () => void;
}

export const GameControls: React.FC<GameControlsProps> = ({
  onFlipBoard,
  onOfferDraw,
  onResign,
}) => {
  return (
    <fieldset
      style={{
        display: 'flex',
        gap: '0.5rem',
        justifyContent: 'flex-start',
        border: 'none',
        padding: '0.5rem 0',
        margin: 0,
        width: '700px',
      }}
      aria-label="Game controls"
    >
      <button
        onClick={onFlipBoard}
        title="Flip Board"
        aria-label="Flip board orientation"
        style={{
          padding: '0.5rem',
          width: '36px',
          height: '36px',
          backgroundColor: '#3a3a3a',
          color: '#e0e0e0',
          border: '2px solid transparent',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '1.1rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s',
        }}
        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#4a4a4a'}
        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#3a3a3a'}
        onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
        onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
      >
        ⇅
      </button>

      <button
        onClick={onOfferDraw}
        title="Offer Draw"
        aria-label="Offer draw to opponent"
        style={{
          padding: '0.5rem',
          width: '36px',
          height: '36px',
          backgroundColor: '#3a3a3a',
          color: '#e0e0e0',
          border: '2px solid transparent',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '1.1rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s',
        }}
        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#4a4a4a'}
        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#3a3a3a'}
        onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
        onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
      >
        🤝
      </button>

      <button
        onClick={onResign}
        title="Resign"
        aria-label="Resign from game"
        style={{
          padding: '0.5rem',
          width: '36px',
          height: '36px',
          backgroundColor: '#f44336',
          color: '#fff',
          border: '2px solid transparent',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '1.1rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s',
        }}
        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#d32f2f'}
        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#f44336'}
        onFocus={(e) => e.currentTarget.style.borderColor = '#4a9eff'}
        onBlur={(e) => e.currentTarget.style.borderColor = 'transparent'}
      >
        🏳
      </button>
    </fieldset>
  );
};
