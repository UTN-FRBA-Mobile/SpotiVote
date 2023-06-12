import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MongooseModule } from '@nestjs/mongoose';
import { PlaylistModule } from './playlist/playlist.module';

@Module({
  imports: [MongooseModule.forRoot(process.env.MONGODB_URI), PlaylistModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
