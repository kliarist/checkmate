import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DifficultySelector, type Difficulty } from '../components/computer/DifficultySelector';
import { createComputerGame } from '../api/gameApi';
import { menuStyles } from '../styles/menuStyles';

export const ComputerGamePage = () => {
  const navigate = useNavigate();
  const [difficulty, setDifficulty] = useState<Difficulty>('intermediate');
  const [playerColor, setPlayerColor] = useState<'white' | 'black'>('white');
  const [isCreatingGame, setIsCreatingGame] = useState(false);

  // Check if user is logged in
  const isLoggedIn = !!localStorage.getItem('token');
  const userName = localStorage.getItem('username');
  const userEmail = localStorage.getItem('userEmail');

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  const handleCreateGame = async (selectedDifficulty: Difficulty, selectedColor: 'white' | 'black') => {
    setIsCreatingGame(true);
    try {
      const response = await createComputerGame({
        difficulty: selectedDifficulty,
        playerColor: selectedColor,
      });
      
      // Navigate to the game page with the gameId in the URL
      navigate(`/game/${response.gameId}`);
    } catch (err: any) {
      console.error('Error creating game:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to create game';
      
      // Check if it's an authentication error
      if (err.response?.status === 401 || err.response?.status === 403) {
        alert('You must be logged in to play against the computer. Please sign in first.');
        navigate('/login');
      } else {
        alert(`Failed to create game: ${errorMessage}`);
      }
    } finally {
      setIsCreatingGame(false);
    }
  };

  return (
    <div style={menuStyles.container}>
      {/* User info bar at top if logged in */}
      {isLoggedIn && (
        <div style={{
          position: 'absolute',
          top: 0,
          right: 0,
          padding: '1rem 2rem',
          display: 'flex',
          alignItems: 'center',
          gap: '1rem',
        }}>
          <span style={{ color: '#e0e0e0' }}>
            {userName || userEmail}
          </span>
          <button
            onClick={() => navigate('/profile')}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#3a3a3a',
              color: '#e0e0e0',
              border: '1px solid #4a4a4a',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.9rem',
            }}
          >
            Profile
          </button>
          <button
            onClick={handleLogout}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: 'transparent',
              color: '#999',
              border: '1px solid #3a3a3a',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.9rem',
            }}
          >
            Logout
          </button>
        </div>
      )}

      <button onClick={() => navigate('/')} style={menuStyles.backButton}>
        ← Back
      </button>
      <div style={{ ...menuStyles.card, maxWidth: '400px' }}>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center',
          marginBottom: '1rem' 
        }}>
          <svg width="80" height="80" viewBox="0 0 45 45" xmlns="http://www.w3.org/2000/svg">
            <g fill="none" fillRule="evenodd" stroke="#b58863" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M 9,39 L 36,39 L 36,36 L 9,36 L 9,39 z" fill="#b58863"/>
              <path d="M 12,36 L 12,32 L 33,32 L 33,36 L 12,36 z" fill="#b58863"/>
              <path d="M 11,14 L 11,9 L 15,9 L 15,11 L 20,11 L 20,9 L 25,9 L 25,11 L 30,11 L 30,9 L 34,9 L 34,14" fill="#b58863"/>
              <path d="M 34,14 L 31,17 L 31,29.5 L 33,32 L 12,32 L 14,29.5 L 14,17 L 11,14" fill="#b58863"/>
              <path d="M 31,17 L 31,29.5" stroke="#b58863"/>
              <path d="M 14,17 L 14,29.5" stroke="#b58863"/>
            </g>
          </svg>
        </div>

        <h1 style={{
          fontSize: '1.5rem',
          fontWeight: '400',
          letterSpacing: '0.3em',
          margin: '0 0 2rem 0',
          color: '#e0e0e0',
          textTransform: 'uppercase',
          textAlign: 'center',
        }}>
          CheckMate
        </h1>

        <h2 style={{
          ...menuStyles.title,
          fontSize: '1.2rem',
        }}>
          Play vs Computer
        </h2>
        
        <div style={{ marginBottom: '2rem' }}>
          <DifficultySelector
            selectedDifficulty={difficulty}
            onSelect={setDifficulty}
          />
        </div>

        <div style={{ marginBottom: '2rem' }}>
          <h3 style={{ marginBottom: '1rem', fontSize: '1.1rem', color: '#e0e0e0' }}>Select Color</h3>
          <div style={{ display: 'flex', gap: '1rem' }}>
            {(['white', 'black'] as const).map((color) => (
              <button
                key={color}
                onClick={() => setPlayerColor(color)}
                style={{
                  flex: 1,
                  padding: '1rem',
                  backgroundColor: playerColor === color ? '#b58863' : '#3d3d3d',
                  color: '#e0e0e0',
                  border: playerColor === color ? '2px solid #c9a068' : '2px solid transparent',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  fontSize: '1rem',
                  textTransform: 'capitalize',
                  transition: 'all 0.2s',
                }}
                onMouseEnter={(e) => {
                  if (playerColor !== color) {
                    e.currentTarget.style.backgroundColor = '#4a4a4a';
                  }
                }}
                onMouseLeave={(e) => {
                  if (playerColor !== color) {
                    e.currentTarget.style.backgroundColor = '#3d3d3d';
                  }
                }}
              >
                {color}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={() => handleCreateGame(difficulty, playerColor)}
          disabled={isCreatingGame}
          style={isCreatingGame ? menuStyles.disabledButton : menuStyles.primaryButton}
        >
          {isCreatingGame ? 'Creating Game...' : 'Start Game'}
        </button>
      </div>
    </div>
  );
};
