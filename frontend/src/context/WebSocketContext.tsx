import React, { createContext, useContext, useEffect, useState, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface WebSocketContextType {
  isConnected: boolean;
  subscribe: (destination: string, callback: (message: any) => void) => () => void;
  send: (destination: string, body: any) => void;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const WebSocketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isConnected, setIsConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<string, any>>(new Map());

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/ws`),
      onConnect: () => {
        console.log('[WebSocket] Connected');
        setIsConnected(true);
      },
      onDisconnect: () => {
        console.log('[WebSocket] Disconnected');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('[WebSocket] STOMP error', frame);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      subscriptionsRef.current.forEach((sub) => sub.unsubscribe());
      subscriptionsRef.current.clear();
      client.deactivate();
    };
  }, []);

  const subscribe = useCallback((destination: string, callback: (message: any) => void) => {
    if (!clientRef.current?.connected) {
      console.log('[WebSocket] Cannot subscribe, not connected');
      return () => {};
    }

    console.log('[WebSocket] Subscribing to:', destination);
    const subscription = clientRef.current.subscribe(destination, (message) => {
      console.log('[WebSocket] Message received on', destination, ':', message.body);
      const body = JSON.parse(message.body);
      callback(body);
    });

    subscriptionsRef.current.set(destination, subscription);

    return () => {
      console.log('[WebSocket] Unsubscribing from:', destination);
      subscription.unsubscribe();
      subscriptionsRef.current.delete(destination);
    };
  }, []);

  const send = useCallback((destination: string, body: any) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.warn('[WebSocket] Cannot send, not connected');
    }
  }, []);

  return (
    <WebSocketContext.Provider value={{ isConnected, subscribe, send }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (context === undefined) {
    throw new Error('useWebSocket must be used within a WebSocketProvider');
  }
  return context;
};

