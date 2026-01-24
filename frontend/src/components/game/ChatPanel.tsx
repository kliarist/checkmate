import { useState } from 'react';

export const ChatPanel = () => {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState<Array<{ user: string; text: string }>>([]);

  const handleSendMessage = () => {
    if (message.trim()) {
      setMessages([...messages, { user: 'You', text: message }]);
      setMessage('');
    }
  };

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
        Chat
      </div>

      <div style={{
        flex: 1,
        padding: '1rem',
        overflowY: 'auto',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
      }}>
        {messages.length === 0 ? (
          <div style={{ color: '#666', fontSize: '0.9rem', textAlign: 'center', marginTop: '2rem' }}>
            No messages yet
          </div>
        ) : (
          messages.map((msg, idx) => (
            <div key={idx} style={{
              backgroundColor: '#1a1a1a',
              padding: '0.5rem 0.75rem',
              borderRadius: '4px',
              fontSize: '0.9rem',
            }}>
              <span style={{ color: '#b58863', fontWeight: '500' }}>{msg.user}:</span>{' '}
              <span style={{ color: '#e0e0e0' }}>{msg.text}</span>
            </div>
          ))
        )}
      </div>

      <div style={{
        padding: '0.75rem',
        borderTop: '1px solid #3a3a3a',
        display: 'flex',
        gap: '0.5rem',
      }}>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
          placeholder="Type a message..."
          style={{
            flex: 1,
            padding: '0.5rem',
            backgroundColor: '#1a1a1a',
            border: '1px solid #3a3a3a',
            borderRadius: '4px',
            color: '#e0e0e0',
            outline: 'none',
            fontSize: '0.9rem',
          }}
        />
        <button
          onClick={handleSendMessage}
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#b58863',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: '500',
          }}
        >
          Send
        </button>
      </div>

      <style>{`
        ::placeholder { color: #666; }
      `}</style>
    </div>
  );
};

