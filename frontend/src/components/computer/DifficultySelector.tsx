import React from 'react';

export type Difficulty = 'beginner' | 'intermediate' | 'advanced';

interface DifficultySelectorProps {
  selectedDifficulty: Difficulty;
  onSelect: (difficulty: Difficulty) => void;
}

const difficultyDescriptions: Record<Difficulty, string> = {
  beginner: 'Easy - Perfect for learning',
  intermediate: 'Medium - A good challenge',
  advanced: 'Hard - For experienced players',
};

export const DifficultySelector: React.FC<DifficultySelectorProps> = ({
  selectedDifficulty,
  onSelect,
}) => {
  const difficulties: Difficulty[] = ['beginner', 'intermediate', 'advanced'];

  return (
    <div style={{ width: '100%' }}>
      <h3 style={{ marginBottom: '1rem', color: '#e0e0e0' }}>Select Difficulty</h3>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        {difficulties.map((difficulty) => (
          <button
            key={difficulty}
            onClick={() => onSelect(difficulty)}
            style={{
              padding: '1rem',
              backgroundColor: selectedDifficulty === difficulty ? '#b58863' : '#3d3d3d',
              color: '#e0e0e0',
              border: selectedDifficulty === difficulty ? '2px solid #c9a068' : '2px solid transparent',
              borderRadius: '8px',
              cursor: 'pointer',
              fontSize: '1rem',
              textAlign: 'left',
              transition: 'all 0.2s',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.25rem',
            }}
            onMouseEnter={(e) => {
              if (selectedDifficulty !== difficulty) {
                e.currentTarget.style.backgroundColor = '#4a4a4a';
              }
            }}
            onMouseLeave={(e) => {
              if (selectedDifficulty !== difficulty) {
                e.currentTarget.style.backgroundColor = '#3d3d3d';
              }
            }}
          >
            <span style={{ fontWeight: 'bold', textTransform: 'capitalize' }}>
              {difficulty}
            </span>
            <span style={{ fontSize: '0.875rem', opacity: 0.8 }}>
              {difficultyDescriptions[difficulty]}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};
