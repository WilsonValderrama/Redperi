import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private client: Client | null = null;

  connect(onLike: (postId: number, totalLikes: number) => void) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('/ws') as any,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('WS conectado ✅');
        this.client?.subscribe('/topic/likes', (message) => {
          console.log('WS mensaje recibido:', message.body);
          const data = JSON.parse(message.body);
          onLike(data.postId, data.totalLikes);
        });
      },
      onStompError: (frame) => {
        console.error('WS error STOMP:', frame);
      },
      onWebSocketError: (evt) => {
        console.error('WS error de socket:', evt);
      },
    });
    this.client.activate();
  }

  disconnect() {
    this.client?.deactivate();
  }
}