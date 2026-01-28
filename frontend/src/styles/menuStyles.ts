/**
 * Shared styles for all menu pages to ensure consistency
 */

export const menuStyles = {
  // Container styles
  container: {
    display: 'flex' as const,
    justifyContent: 'center' as const,
    alignItems: 'center' as const,
    minHeight: '100vh',
    backgroundColor: '#242424',
    color: '#e0e0e0',
  },

  // Back button
  backButton: {
    position: 'absolute' as const,
    top: '1rem',
    left: '1rem',
    padding: '0.5rem 1rem',
    backgroundColor: '#3a3a3a',
    color: '#e0e0e0',
    border: '1px solid #4a4a4a',
    borderRadius: '4px',
    cursor: 'pointer' as const,
    fontSize: '0.9rem',
  },

  // Card container
  card: {
    backgroundColor: '#2d2d2d',
    padding: '3rem',
    borderRadius: '12px',
    maxWidth: '500px',
    width: '100%',
  },

  // Title
  title: {
    marginBottom: '1rem',
    textAlign: 'center' as const,
    fontSize: '1.8rem',
    fontWeight: '400' as const,
  },

  // Subtitle
  subtitle: {
    marginBottom: '2rem',
    color: '#999',
    textAlign: 'center' as const,
    fontSize: '0.95rem',
  },

  // Primary button
  primaryButton: {
    width: '100%',
    padding: '1rem',
    backgroundColor: '#b58863',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer' as const,
    fontSize: '1.1rem',
    fontWeight: 'bold' as const,
    marginBottom: '1rem',
  },

  // Secondary button
  secondaryButton: {
    width: '100%',
    padding: '1rem',
    backgroundColor: '#8b6f47',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer' as const,
    fontSize: '1.1rem',
    fontWeight: 'bold' as const,
  },

  // Accent button (for ranked/special features)
  accentButton: {
    width: '100%',
    padding: '1rem',
    backgroundColor: '#b58863',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer' as const,
    fontSize: '1.1rem',
    fontWeight: 'bold' as const,
  },

  // Disabled button
  disabledButton: {
    width: '100%',
    padding: '1rem',
    backgroundColor: '#666',
    color: '#999',
    border: 'none',
    borderRadius: '8px',
    cursor: 'not-allowed' as const,
    fontSize: '1.1rem',
    fontWeight: 'bold' as const,
  },

  // Input field
  input: {
    width: '100%',
    padding: '0.75rem',
    backgroundColor: '#3d3d3d',
    color: '#e0e0e0',
    border: '1px solid #555',
    borderRadius: '8px',
    fontSize: '1rem',
    marginBottom: '1rem',
    boxSizing: 'border-box' as const,
  },

  // Error message
  errorBox: {
    padding: '1rem',
    backgroundColor: '#ff6b6b',
    borderRadius: '8px',
    marginBottom: '1rem',
    color: '#fff',
  },

  // Divider text
  divider: {
    margin: '2rem 0',
    color: '#888',
    textAlign: 'center' as const,
    fontSize: '0.9rem',
  },

  // Info box
  infoBox: {
    marginBottom: '2rem',
    padding: '1rem',
    backgroundColor: '#3d3d3d',
    borderRadius: '8px',
  },

  // Landing page specific button styles
  landingButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Sign in button (accent color)
  signInButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: '#b58863',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Sign up button (secondary style)
  signUpButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: '#3a3a3a',
    color: '#e0e0e0',
    border: '1px solid #4a4a4a',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Computer game button (primary brown)
  computerButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: '#b58863',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Private game button (darker brown)
  privateButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: '#8b6f47',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Ranked game button (lighter brown/gold)
  rankedButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: '#c9a068',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Anonymous/guest button (transparent)
  guestButton: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: 'transparent',
    color: '#999',
    border: '1px solid #3a3a3a',
    borderRadius: '4px',
    cursor: 'pointer' as const,
  },

  // Disabled guest button
  guestButtonDisabled: {
    width: '100%',
    padding: '0.875rem',
    fontSize: '1rem',
    fontWeight: '500' as const,
    backgroundColor: 'transparent',
    color: '#999',
    border: '1px solid #3a3a3a',
    borderRadius: '4px',
    cursor: 'not-allowed' as const,
  },
};
