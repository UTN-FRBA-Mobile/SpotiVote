import { ConfigurableModuleBuilder } from '@nestjs/common';
import { SpotifyModuleOptions } from './spotify-module-options';

export const {
  ConfigurableModuleClass: ConfigurableSpotifyModule,
  MODULE_OPTIONS_TOKEN: SPOTIFY_MODULE_OPTIONS_TOKEN,
  OPTIONS_TYPE,
  ASYNC_OPTIONS_TYPE,
} = new ConfigurableModuleBuilder<SpotifyModuleOptions>().build();
