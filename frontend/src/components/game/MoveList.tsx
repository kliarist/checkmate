import { useEffect, useRef } from 'react';

interface Move {
  notation: string;
  number: number;
}

interface MoveListProps {
  moves: Move[];
  currentMoveIndex: number;
  onMoveClick: (index: number) => void;
  onNext: () => void;
  onPrevious: () => void;
  onResume: () => void;
}

export const MoveList = ({
  moves,
  currentMoveIndex,
  onMoveClick,
  onNext,
  onPrevious,
  onResume
}: MoveListProps) => {

  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const currentMoveRefs = useRef<{ [key: number]: HTMLSpanElement | null }>({});

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft') {
        e.preventDefault();
        onPrevious();
      } else if (e.key === 'ArrowRight') {
        e.preventDefault();
        onNext();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onNext, onPrevious]);

  useEffect(() => {
    if (currentMoveIndex >= 0 && currentMoveRefs.current[currentMoveIndex]) {
      currentMoveRefs.current[currentMoveIndex]?.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
        inline: 'nearest'
      });
    }
  }, [currentMoveIndex]);

  const movePairs: Array<{ turn: number; white?: Move; black?: Move }> = [];

  for (let i = 0; i < moves.length; i += 2) {
    movePairs.push({
      turn: moves[i].number,
      white: moves[i],
      black: moves[i + 1]
    });
  }

  const isAtEnd = currentMoveIndex === moves.length - 1;

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

      <div
        ref={scrollContainerRef}
        style={{
          flex: 1,
          padding: '1rem',
          overflowY: 'auto',
          display: 'flex',
          flexDirection: 'column',
          gap: '0.25rem',
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
          movePairs.map((pair, pairIndex) => (
            <div
              key={pairIndex}
              style={{
                padding: '0.5rem 0.75rem',
                backgroundColor: '#1a1a1a',
                borderRadius: '4px',
                fontSize: '0.9rem',
                display: 'flex',
                gap: '0.5rem',
                alignItems: 'center',
              }}
            >
              <span style={{
                color: '#b58863',
                fontWeight: '500',
                minWidth: '2rem'
              }}>
                {pair.turn}.
              </span>

              {pair.white && (
                <span
                  ref={(el) => { currentMoveRefs.current[pairIndex * 2] = el; }}
                  onClick={() => onMoveClick(pairIndex * 2)}
                  style={{
                    color: '#e0e0e0',
                    cursor: 'pointer',
                    padding: '0.25rem 0.5rem',
                    borderRadius: '3px',
                    backgroundColor: currentMoveIndex === pairIndex * 2 ? '#b58863' : 'transparent',
                    transition: 'background-color 0.2s',
                    flex: 1,
                  }}
                  onMouseEnter={(e) => {
                    if (currentMoveIndex !== pairIndex * 2) {
                      e.currentTarget.style.backgroundColor = '#3a3a3a';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (currentMoveIndex !== pairIndex * 2) {
                      e.currentTarget.style.backgroundColor = 'transparent';
                    }
                  }}
                >
                  {pair.white.notation}
                </span>
              )}

              {pair.black && (
                <span
                  ref={(el) => { currentMoveRefs.current[pairIndex * 2 + 1] = el; }}
                  onClick={() => onMoveClick(pairIndex * 2 + 1)}
                  style={{
                    color: '#e0e0e0',
                    cursor: 'pointer',
                    padding: '0.25rem 0.5rem',
                    borderRadius: '3px',
                    backgroundColor: currentMoveIndex === pairIndex * 2 + 1 ? '#b58863' : 'transparent',
                    transition: 'background-color 0.2s',
                    flex: 1,
                  }}
                  onMouseEnter={(e) => {
                    if (currentMoveIndex !== pairIndex * 2 + 1) {
                      e.currentTarget.style.backgroundColor = '#3a3a3a';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (currentMoveIndex !== pairIndex * 2 + 1) {
                      e.currentTarget.style.backgroundColor = 'transparent';
                    }
                  }}
                >
                  {pair.black.notation}
                </span>
              )}
            </div>
          ))
        )}
      </div>

      {moves.length > 0 && (
        <div style={{
          padding: '0.75rem',
          borderTop: '1px solid #3a3a3a',
          display: 'flex',
          gap: '0.5rem',
          justifyContent: 'center',
        }}>
          <button
            onClick={() => onMoveClick(-1)}
            disabled={currentMoveIndex === -1}
            title="Go to start (first move)"
            aria-label="Go to start"
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: currentMoveIndex === -1 ? '#1a1a1a' : '#3a3a3a',
              color: currentMoveIndex === -1 ? '#666' : '#e0e0e0',
              border: 'none',
              borderRadius: '4px',
              cursor: currentMoveIndex === -1 ? 'not-allowed' : 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
              transition: 'background-color 0.2s',
              minWidth: '40px',
            }}
            onMouseEnter={(e) => {
              if (currentMoveIndex !== -1) {
                e.currentTarget.style.backgroundColor = '#4a4a4a';
              }
            }}
            onMouseLeave={(e) => {
              if (currentMoveIndex !== -1) {
                e.currentTarget.style.backgroundColor = '#3a3a3a';
              }
            }}
          >
            ⏮
          </button>

          <button
            onClick={onPrevious}
            disabled={currentMoveIndex < 0}
            title="Previous move (← Left Arrow)"
            aria-label="Previous move"
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: currentMoveIndex < 0 ? '#1a1a1a' : '#3a3a3a',
              color: currentMoveIndex < 0 ? '#666' : '#e0e0e0',
              border: 'none',
              borderRadius: '4px',
              cursor: currentMoveIndex < 0 ? 'not-allowed' : 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
              transition: 'background-color 0.2s',
              minWidth: '40px',
            }}
            onMouseEnter={(e) => {
              if (currentMoveIndex >= 0) {
                e.currentTarget.style.backgroundColor = '#4a4a4a';
              }
            }}
            onMouseLeave={(e) => {
              if (currentMoveIndex >= 0) {
                e.currentTarget.style.backgroundColor = '#3a3a3a';
              }
            }}
          >
            ◀
          </button>

          <button
            onClick={onResume}
            disabled={isAtEnd}
            title="Jump to current position"
            aria-label="Resume to current"
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: isAtEnd ? '#1a1a1a' : '#b58863',
              color: isAtEnd ? '#666' : '#fff',
              border: 'none',
              borderRadius: '4px',
              cursor: isAtEnd ? 'not-allowed' : 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
              transition: 'background-color 0.2s',
              minWidth: '40px',
            }}
            onMouseEnter={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#c69873';
              }
            }}
            onMouseLeave={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#b58863';
              }
            }}
          >
            ⏺
          </button>

          <button
            onClick={onNext}
            disabled={isAtEnd}
            title="Next move (→ Right Arrow)"
            aria-label="Next move"
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: isAtEnd ? '#1a1a1a' : '#3a3a3a',
              color: isAtEnd ? '#666' : '#e0e0e0',
              border: 'none',
              borderRadius: '4px',
              cursor: isAtEnd ? 'not-allowed' : 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
              transition: 'background-color 0.2s',
              minWidth: '40px',
            }}
            onMouseEnter={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#4a4a4a';
              }
            }}
            onMouseLeave={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#3a3a3a';
              }
            }}
          >
            ▶
          </button>

          <button
            onClick={() => onMoveClick(moves.length - 1)}
            disabled={isAtEnd}
            title="Go to end (latest move)"
            aria-label="Go to end"
            style={{
              padding: '0.5rem 0.75rem',
              backgroundColor: isAtEnd ? '#1a1a1a' : '#3a3a3a',
              color: isAtEnd ? '#666' : '#e0e0e0',
              border: 'none',
              borderRadius: '4px',
              cursor: isAtEnd ? 'not-allowed' : 'pointer',
              fontSize: '1rem',
              fontWeight: '500',
              transition: 'background-color 0.2s',
              minWidth: '40px',
            }}
            onMouseEnter={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#4a4a4a';
              }
            }}
            onMouseLeave={(e) => {
              if (!isAtEnd) {
                e.currentTarget.style.backgroundColor = '#3a3a3a';
              }
            }}
          >
            ⏭
          </button>
        </div>
      )}
    </div>
  );
};


