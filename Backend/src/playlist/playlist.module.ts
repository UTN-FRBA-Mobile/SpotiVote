import { Module } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { PlaylistController } from './playlist.controller';
import { MongooseModule } from '@nestjs/mongoose';
import { Playlist, PlaylistSchema } from './schemas/playlist.schema';
import { SpotifyModule } from '@app/spotify';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { SpotifyModuleOptions } from '@app/spotify/interfaces/spotify-module-options';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: Playlist.name, schema: PlaylistSchema },
    ]),
    SpotifyModule.registerAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: async (
        configService: ConfigService,
      ): Promise<SpotifyModuleOptions> => ({
        clientId: process.env.SPOTIFY_CLIENT_ID,
        clientSecret: process.env.SPOTIFY_CLIENT_SECRET,
      }),
    }),
  ],
  controllers: [PlaylistController],
  providers: [PlaylistService],
})
export class PlaylistModule {}
