import { ReactNode } from 'react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
  actions?: ReactNode;
}

export const Modal = ({ isOpen, onClose, title, children, actions }: ModalProps) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.85)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999,
      }}
      onClick={onClose}
    >
      <div
        style={{
          backgroundColor: '#2a2a2a',
          borderRadius: '8px',
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
          maxWidth: '500px',
          width: '90%',
          maxHeight: '90vh',
          overflow: 'auto',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          style={{
            padding: '1.5rem 2rem',
            borderBottom: '1px solid #3a3a3a',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <h2
            style={{
              margin: 0,
              fontSize: '1.5rem',
              fontWeight: '500',
              color: '#e0e0e0',
            }}
          >
            {title}
          </h2>
          <button
            onClick={onClose}
            aria-label="Close modal"
            style={{
              backgroundColor: 'transparent',
              border: 'none',
              color: '#e0e0e0',
              fontSize: '1.5rem',
              cursor: 'pointer',
              padding: '0',
              width: '32px',
              height: '32px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: '4px',
              transition: 'background-color 0.2s',
            }}
            onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#3a3a3a'}
            onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
          >
            ✕
          </button>
        </div>

        <div
          style={{
            padding: '2rem',
            color: '#e0e0e0',
          }}
        >
          {children}
        </div>

        {actions && (
          <div
            style={{
              padding: '1rem 2rem',
              borderTop: '1px solid #3a3a3a',
              display: 'flex',
              gap: '0.75rem',
              justifyContent: 'flex-end',
            }}
          >
            {actions}
          </div>
        )}
      </div>
    </div>
  );
};

interface ModalButtonProps {
  onClick: () => void;
  variant?: 'primary' | 'secondary' | 'danger';
  children: ReactNode;
  disabled?: boolean;
}

export const ModalButton = ({
  onClick,
  variant = 'secondary',
  children,
  disabled = false
}: ModalButtonProps) => {
  const getBackgroundColor = () => {
    if (disabled) return '#1a1a1a';
    switch (variant) {
      case 'primary':
        return '#b58863';
      case 'danger':
        return '#e74c3c';
      case 'secondary':
      default:
        return '#3a3a3a';
    }
  };

  const getHoverColor = () => {
    switch (variant) {
      case 'primary':
        return '#c69873';
      case 'danger':
        return '#c0392b';
      case 'secondary':
      default:
        return '#4a4a4a';
    }
  };

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      style={{
        padding: '0.75rem 1.5rem',
        backgroundColor: getBackgroundColor(),
        color: disabled ? '#666' : '#fff',
        border: 'none',
        borderRadius: '4px',
        cursor: disabled ? 'not-allowed' : 'pointer',
        fontSize: '0.95rem',
        fontWeight: '500',
        transition: 'background-color 0.2s',
      }}
      onMouseEnter={(e) => {
        if (!disabled) {
          e.currentTarget.style.backgroundColor = getHoverColor();
        }
      }}
      onMouseLeave={(e) => {
        if (!disabled) {
          e.currentTarget.style.backgroundColor = getBackgroundColor();
        }
      }}
    >
      {children}
    </button>
  );
};

