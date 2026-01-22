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
      height: '100%',
      backgroundColor: '#2a2a2a',
      borderRadius: '4px',
      overflow: 'hidden',
    }}>
      <div style={{
        padding: '0.75rem 1rem',
        backgroundColor: '#1a1a1a',
        borderBottom: '1px solid #3a3a3a',
        fontWeight: '500',
        color: '#e0e0e0',
      }}>
        Move History
      </div>

      <div style={{
        flex: 1,
        padding: '1rem',
        overflowY: 'auto',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
      }}>
        {moves.length === 0 ? (
          <div style={{
            color: '#666',
            fontSize: '0.9rem',
            textAlign: 'center',
            marginTop: '2rem'
          }}>
            No moves yet
          </div>
        ) : (
          moves.map((move, index) => (
            <div
              key={index}
              style={{
                padding: '0.5rem 0.75rem',
                backgroundColor: '#1a1a1a',
                borderRadius: '4px',
                fontSize: '0.9rem',
              }}
            >
              <span style={{ color: '#b58863', fontWeight: '500' }}>
                {move.number}.
              </span>{' '}
              <span style={{ color: '#e0e0e0' }}>
                {move.notation}
              </span>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
