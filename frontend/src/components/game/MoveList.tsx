interface Move {
  notation: string;
  number: number;
}

interface MoveListProps {
  moves: Move[];
}

export const MoveList = ({ moves }: MoveListProps) => {
  return (
    <div style={{ maxHeight: '400px', overflowY: 'auto', border: '1px solid #ccc', padding: '1rem' }}>
      {moves.length === 0 && <p>No moves yet</p>}
      {moves.map((move, index) => (
        <div key={index} style={{ marginBottom: '0.5rem' }}>
          {move.number}. {move.notation}
        </div>
      ))}
    </div>
  );
};

