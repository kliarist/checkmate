interface GameEndModalProps {
  result: string;
  onClose: () => void;
}

export const GameEndModal = ({ result, onClose }: GameEndModalProps) => {
  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <div
        style={{
          backgroundColor: 'white',
          padding: '2rem',
          borderRadius: '8px',
          textAlign: 'center',
        }}
      >
        <h2>Game Over</h2>
        <p style={{ fontSize: '1.5rem', margin: '1rem 0' }}>{result}</p>
        <button
          onClick={onClose}
          style={{
            padding: '0.5rem 2rem',
            backgroundColor: '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          Play Again
        </button>
      </div>
    </div>
  );
};

