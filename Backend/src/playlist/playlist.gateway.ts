import {
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
  WsResponse,
} from '@nestjs/websockets';
import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Server } from 'socket.io';

@WebSocketGateway(80, {
  transports: ['websocket'],
  cors: { origin: '*' },
})
export class PlaylistGateway {
  @WebSocketServer()
  server: Server;

  @SubscribeMessage('events')
  handleEvent(@MessageBody() data: string): string {
    return data;
  }

  async sendMessage(songId: string, likes: number) {
    return await this.server.emit('identity', { songId, likes });
  }

  @SubscribeMessage('identity')
  async identity(@MessageBody() data) {
    return data;
  }
}
