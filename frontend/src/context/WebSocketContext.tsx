import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from './AuthContext';

interface WebSocketContextType {
  isConnected: boolean;
  subscribe: (destination: string, callback: (message: any) => void) => () => void;
  send: (destination: string, body: any) => void;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const WebSocketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { token } = useAuth();
  const [isConnected, setIsConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<string, StompSubscription>>(new Map());

  useEffect(() => {
    if (!token) {
      if (clientRef.current) {
        clientRef.current.deactivate();
        setIsConnected(false);
      }
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_URL}/ws`),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        setIsConnected(true);
      },
      onDisconnect: () => {
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      subscriptionsRef.current.forEach((sub) => sub.unsubscribe());
      subscriptionsRef.current.clear();
      client.deactivate();
    };
  }, [token]);

  const subscribe = (destination: string, callback: (message: any) => void) => {
    if (!clientRef.current || !isConnected) {
      return () => {};
    }

    const subscription = clientRef.current.subscribe(destination, (message) => {
      const body = JSON.parse(message.body);
      callback(body);
    });

    subscriptionsRef.current.set(destination, subscription);

    return () => {
      subscription.unsubscribe();
      subscriptionsRef.current.delete(destination);
    };
  };

  const send = (destination: string, body: any) => {
    if (clientRef.current && isConnected) {
      clientRef.current.publish({
        destination,
        body: JSON.stringify(body),
      });
    }
  };

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

