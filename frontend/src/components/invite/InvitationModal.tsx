import React, { useState } from 'react';

interface InvitationModalProps {
  invitationLink: string;
  invitationCode: string;
  onClose: () => void;
}

export const InvitationModal: React.FC<InvitationModalProps> = ({
  invitationLink,
  invitationCode,
  onClose,
}) => {
  const [copied, setCopied] = useState(false);

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(invitationLink);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  const handleCopyCode = async () => {
    try {
      await navigator.clipboard.writeText(invitationCode);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000,
    }}>
      <div style={{
        backgroundColor: '#2d2d2d',
        padding: '2rem',
        borderRadius: '12px',
        maxWidth: '500px',
        width: '90%',
        color: '#e0e0e0',
      }}>
        <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>
          Invitation Created!
        </h2>

        <p style={{ marginBottom: '1rem', color: '#aaa', textAlign: 'center' }}>
          Share this link or code with your friend to start playing
        </p>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{
            display: 'block',
            marginBottom: '0.5rem',
            fontSize: '0.9rem',
            color: '#888',
          }}>
            Invitation Code
          </label>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <input
              type="text"
              value={invitationCode}
              readOnly
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: '#3d3d3d',
                color: '#e0e0e0',
                border: '1px solid #555',
                borderRadius: '8px',
                fontSize: '1.2rem',
                fontWeight: 'bold',
                textAlign: 'center',
                letterSpacing: '0.1em',
              }}
            />
            <button
              onClick={handleCopyCode}
              style={{
                padding: '0.75rem 1.5rem',
                backgroundColor: copied ? '#c9a068' : '#b58863',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
                cursor: 'pointer',
                fontWeight: 'bold',
                transition: 'background-color 0.2s',
              }}
            >
              {copied ? '✓' : 'Copy'}
            </button>
          </div>
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{
            display: 'block',
            marginBottom: '0.5rem',
            fontSize: '0.9rem',
            color: '#888',
          }}>
            Invitation Link
          </label>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <input
              type="text"
              value={invitationLink}
              readOnly
              style={{
                flex: 1,
                padding: '0.75rem',
                backgroundColor: '#3d3d3d',
                color: '#e0e0e0',
                border: '1px solid #555',
                borderRadius: '8px',
                fontSize: '0.9rem',
              }}
            />
            <button
              onClick={handleCopyLink}
              style={{
                padding: '0.75rem 1.5rem',
                backgroundColor: copied ? '#c9a068' : '#b58863',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
                cursor: 'pointer',
                fontWeight: 'bold',
                transition: 'background-color 0.2s',
              }}
            >
              {copied ? '✓' : 'Copy'}
            </button>
          </div>
        </div>

        <div style={{
          padding: '1rem',
          backgroundColor: '#3d3d3d',
          borderRadius: '8px',
          marginBottom: '1.5rem',
          fontSize: '0.9rem',
          color: '#aaa',
        }}>
          <p style={{ margin: 0 }}>
            ⏱️ This invitation expires in 10 minutes
          </p>
        </div>

        <button
          onClick={onClose}
          style={{
            width: '100%',
            padding: '0.75rem',
            backgroundColor: '#666',
            color: '#fff',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '1rem',
          }}
        >
          Close
        </button>
      </div>
    </div>
  );
};
