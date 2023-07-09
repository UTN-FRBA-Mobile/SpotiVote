import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DeviceTokenModule } from './device-token/device-token.module';
import { RoomModule } from './room/room.module';
import { PlaylistModule } from './playlist/playlist.module';

@Module({
  imports: [
    MongooseModule.forRoot(process.env.MONGODB_URI),
    PlaylistModule,
    RoomModule,
    DeviceTokenModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
