import { SpotifyModule } from '@app/spotify';
import { SpotifyModuleOptions } from '@app/spotify/interfaces/spotify-module-options';
import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { PlaylistController } from './playlist.controller';
import { PlaylistService } from './playlist.service';
import { DeviceToken, DeviceTokenSchema } from './schemas/device-token.schema';
import { Playlist, PlaylistSchema } from './schemas/playlist.schema';
import { Song, SongSchema } from './schemas/song.schema';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: Playlist.name, schema: PlaylistSchema },
      { name: Song.name, schema: SongSchema },
      { name: DeviceToken.name, schema: DeviceTokenSchema },
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
