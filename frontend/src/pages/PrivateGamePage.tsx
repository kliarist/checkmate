import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ChessBoard } from '../components/game/ChessBoard';
import { MoveList } from '../components/game/MoveList';
import { GameEndModal } from '../components/game/GameEndModal';
import { InvitationModal } from '../components/invite/InvitationModal';
import { useChessGame } from '../hooks/useChessGame';
import apiClient from '../api/client';
import { menuStyles } from '../styles/menuStyles';

export const PrivateGamePage = () => {
  const { code } = useParams<{ code?: string }>();
  const navigate = useNavigate();
  const [gameId, setGameId] = useState<string | null>(null);
  const [showInvitationModal, setShowInvitationModal] = useState(false);
  const [invitationLink, setInvitationLink] = useState('');
  const [invitationCode, setInvitationCode] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [isJoining, setIsJoining] = useState(false);
  const [waitingForOpponent, setWaitingForOpponent] = useState(false);
  const [error, setError] = useState('');

  // Check if user is logged in
  const isLoggedIn = !!localStorage.getItem('token');
  const userName = localStorage.getItem('username');
  const userEmail = localStorage.getItem('userEmail');

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  const {
    fen,
    moves,
    currentMoveIndex,
    isGameOver,
    result,
    makeMove,
    goToMove,
    nextMove,
    previousMove,
    resumeGame,
    loading,
    error: gameError,
    playerColor,
    lastMove,
  } = useChessGame(gameId || '');

  // Auto-join if code in URL
  useEffect(() => {
    if (code && !gameId && !isJoining) {
      handleJoinInvitation(code);
    }
  }, [code]);

  const handleCreateInvitation = async () => {
    setIsCreating(true);
    setError('');
    try {
      const response = await apiClient.post('/api/games/invite', {
        timeControl: '10+0',
        gameType: 'PRIVATE',
      });

      const data = response.data.data;
      setInvitationLink(data.invitationLink);
      setInvitationCode(data.invitationCode);
      setShowInvitationModal(true);
      setWaitingForOpponent(true);
      
      // Poll for game start (in production, use WebSocket)
      pollForGameStart(data.invitationCode);
    } catch (err: any) {
      console.error('Error creating invitation:', err);
      setError(err.response?.data?.message || 'Failed to create invitation');
    } finally {
      setIsCreating(false);
    }
  };

  const handleJoinInvitation = async (joinCode: string) => {
    setIsJoining(true);
    setError('');
    try {
      const response = await apiClient.post(`/api/games/join/${joinCode}`);
      const data = response.data.data;
      
      setGameId(data.gameId);
      navigate(`/game/${data.gameId}`);
    } catch (err: any) {
      console.error('Error joining invitation:', err);
      setError(err.response?.data?.message || 'Failed to join game. Invitation may be expired or invalid.');
    } finally {
      setIsJoining(false);
    }
  };

  const pollForGameStart = async (code: string) => {
    // Simple polling - in production use WebSocket
    const interval = setInterval(async () => {
      try {
        const response = await apiClient.get(`/api/games/invitation/${code}/status`);
        if (response.data.data.status === 'USED') {
          clearInterval(interval);
          setGameId(response.data.data.gameId);
          setWaitingForOpponent(false);
        }
      } catch (err) {
        // Invitation might be expired
        clearInterval(interval);
        setWaitingForOpponent(false);
        setError('Invitation expired');
      }
    }, 2000);

    // Stop polling after 10 minutes
    setTimeout(() => clearInterval(interval), 600000);
  };

  if (!gameId && !showInvitationModal) {
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
            Play with a Friend
          </h2>
          
          {error && (
            <div style={menuStyles.errorBox}>
              {error}
            </div>
          )}

          <button
            onClick={handleCreateInvitation}
            disabled={isCreating}
            style={isCreating ? menuStyles.disabledButton : menuStyles.primaryButton}
          >
            {isCreating ? 'Creating Invitation...' : 'Create Invitation'}
          </button>

          <div style={menuStyles.divider}>OR</div>

          <div>
            <input
              type="text"
              placeholder="Enter invitation code"
              value={invitationCode}
              onChange={(e) => setInvitationCode(e.target.value.toUpperCase())}
              style={menuStyles.input}
            />
            <button
              onClick={() => handleJoinInvitation(invitationCode)}
              disabled={isJoining || !invitationCode}
              style={isJoining || !invitationCode ? menuStyles.disabledButton : menuStyles.secondaryButton}
            >
              {isJoining ? 'Joining...' : 'Join Game'}
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (waitingForOpponent) {
    return (
      <>
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

          <div style={{ ...menuStyles.card, maxWidth: '400px', textAlign: 'center' }}>
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

            <h2 style={{ marginBottom: '2rem', fontSize: '1.2rem', color: '#e0e0e0' }}>Waiting for opponent...</h2>
            <div style={{
              animation: 'pulse 1.5s ease-in-out infinite',
              fontSize: '3rem',
            }}>
              ⏳
            </div>
            <p style={{ marginTop: '2rem', color: '#888' }}>
              Share the invitation link with your friend
            </p>
          </div>
        </div>
        {showInvitationModal && (
          <InvitationModal
            invitationLink={invitationLink}
            invitationCode={invitationCode}
            onClose={() => {
              setShowInvitationModal(false);
              navigate('/');
            }}
          />
        )}
        <style>{`
          @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
          }
        `}</style>
      </>
    );
  }

  if (loading) {
    return (
      <div style={menuStyles.container}>
        Loading game...
      </div>
    );
  }

  if (gameError && !fen) {
    return (
      <div style={{
        ...menuStyles.container,
        textAlign: 'center',
        padding: '2rem',
        color: '#ff6b6b',
      }}>
        Error loading game: {gameError}
      </div>
    );
  }

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      width: '100vw',
      height: '100vh',
      backgroundColor: '#242424',
      padding: '2rem',
      boxSizing: 'border-box',
      overflow: 'hidden',
    }}>
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'minmax(auto, 700px) minmax(300px, 350px)',
        gap: '2rem',
        maxWidth: '1400px',
      }}>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
        }}>
          <div style={{
            marginBottom: '1rem',
            padding: '0.5rem 1rem',
            backgroundColor: '#3d3d3d',
            borderRadius: '8px',
            color: '#e0e0e0',
          }}>
            Private Game
          </div>
          <ChessBoard
            fen={fen}
            onMove={makeMove}
            playerColor={playerColor}
            lastMove={lastMove}
          />
        </div>

        <div style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
          height: 'calc(100vh - 4rem)',
          overflow: 'hidden',
        }}>
          <MoveList
            moves={moves}
            currentMoveIndex={currentMoveIndex}
            onMoveClick={goToMove}
            onNext={nextMove}
            onPrevious={previousMove}
            onResume={resumeGame}
          />
        </div>

        {isGameOver && (
          <GameEndModal 
            result={result} 
            onPlayAgain={() => navigate('/private')}
            onGoToMenu={() => navigate('/')}
          />
        )}
      </div>
    </div>
  );
};
