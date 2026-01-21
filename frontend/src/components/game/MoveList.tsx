interface Move {
  notation: string;
  number: number;
}

interface MoveListProps {
  moves: Move[];
}

export const MoveList = ({ moves }: MoveListProps) => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      gap: '0.25rem',
    }}>
      {moves.length === 0 ? (
        <p style={{
          color: '#666',
          fontSize: '0.9rem',
          textAlign: 'center',
          margin: '1rem 0',
        }}>
          No moves yet
        </p>
      ) : (
        moves.map((move, index) => (
          <div
            key={index}
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: '#1a1a1a',
              borderRadius: '4px',
              fontSize: '0.9rem',
              color: '#e0e0e0',
            }}
          >
            <span style={{ color: '#b58863', fontWeight: '500' }}>
              {move.number}.
            </span>{' '}
            {move.notation}
          </div>
        ))
      )}
    </div>
  );
};
