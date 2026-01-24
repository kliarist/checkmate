import { Client } from '@stomp/stompjs';
import type { StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketClient {
  private client: Client | null = null;
  private subscriptions: Map<string, StompSubscription> = new Map();

  connect(token: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.client = new Client({
        webSocketFactory: () => new SockJS(`${import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws'}`),
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        onConnect: () => {
          resolve();
        },
        onDisconnect: () => {
          this.subscriptions.clear();
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame);
          reject(new Error(frame.headers.message));
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.activate();
    });
  }

  disconnect(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
    this.subscriptions.clear();
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }

  subscribe(destination: string, callback: (message: any) => void): () => void {
    if (!this.client) {
      throw new Error('WebSocket client not connected');
    }

    const subscription = this.client.subscribe(destination, (message) => {
      const body = JSON.parse(message.body);
      callback(body);
    });

    this.subscriptions.set(destination, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
    };
  }

  send(destination: string, body: any): void {
    if (!this.client) {
      throw new Error('WebSocket client not connected');
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    });
  }

  isConnected(): boolean {
    return this.client?.connected || false;
  }
}

export const wsClient = new WebSocketClient();

