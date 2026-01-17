import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const Header = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <header style={{ padding: '1rem', borderBottom: '1px solid #ccc' }}>
      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Link to="/" style={{ marginRight: '1rem' }}>
            Checkmate
          </Link>
          <Link to="/play" style={{ marginRight: '1rem' }}>
            Play
          </Link>
        </div>
        <div>
          {isAuthenticated ? (
            <>
              <span style={{ marginRight: '1rem' }}>{user?.username}</span>
              <button onClick={logout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" style={{ marginRight: '1rem' }}>
                Login
              </Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};

