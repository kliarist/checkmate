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
        backgroundColor: 'rgba(0,0,0,0.7)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999,
      }}
    >
      <div
        style={{
          backgroundColor: 'white',
          padding: '2.5rem 3rem',
          borderRadius: '12px',
          textAlign: 'center',
          boxShadow: '0 8px 32px rgba(0,0,0,0.3)',
          maxWidth: '400px',
        }}
      >
        <h2 style={{ margin: '0 0 1rem 0', fontSize: '1.8rem' }}>Game Over</h2>
        <p style={{ fontSize: '1.5rem', margin: '1rem 0', color: '#333' }}>{result}</p>
        <button
          onClick={onClose}
          style={{
            padding: '0.75rem 2.5rem',
            backgroundColor: '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            cursor: 'pointer',
            fontSize: '1.1rem',
            fontWeight: 'bold',
            transition: 'background-color 0.2s',
          }}
          onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#45a049'}
          onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#4CAF50'}
        >
          Play Again
        </button>
      </div>
    </div>
  );
};
