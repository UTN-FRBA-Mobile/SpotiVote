import { Module } from '@nestjs/common';
import { SpotifyService } from './spotify.service';
import { ConfigurableSpotifyModule } from './interfaces/spotify-module-definition';
import { HttpModule } from '@nestjs/axios';

@Module({
  imports: [HttpModule],
  providers: [SpotifyService],
  exports: [SpotifyService],
})
export class SpotifyModule extends ConfigurableSpotifyModule {}
