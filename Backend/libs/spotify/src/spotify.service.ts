import { Inject, Injectable } from '@nestjs/common';
import { SPOTIFY_MODULE_OPTIONS_TOKEN } from './interfaces/spotify-module-definition';
import { SpotifyModuleOptions } from './interfaces/spotify-module-options';
import { HttpService } from '@nestjs/axios';
import * as crypto from 'crypto';
import { lastValueFrom } from 'rxjs';

@Injectable()
export class SpotifyService {
  public authorized = null;
  constructor(
    @Inject(SPOTIFY_MODULE_OPTIONS_TOKEN) private options: SpotifyModuleOptions,
    private httpService: HttpService,
  ) {}

  private async authorize() {
    if (!this.authorized) {
      // const state = crypto.randomBytes(64).toString('hex');
      // const scope = 'user-read-private user-read-email';
      // const redirect_uri = 'http://localhost:8888/callback';

      // const codeChallenge = (
      //   await lastValueFrom(
      //     this.httpService.post('https://accounts.spotify.com/api/token', {
      //       params: {
      //         response_type: 'code',
      //         client_id: this.options.clientId,
      //         scope: scope,
      //         redirect_uri: redirect_uri,
      //         state: state,
      //       },
      //     }),
      //   )
      // ).data;

      const headers = {
        Authorization: `Basic ${Buffer.from(
          this.options.clientId + ':' + this.options.clientSecret,
        ).toString('base64')}`,
      };

      const data = new URLSearchParams({
        grant_type: 'client_credentials',
      });

      const authTokenResponse = await lastValueFrom(
        this.httpService.post('https://accounts.spotify.com/api/token', data, {
          headers,
        }),
      );

      if (authTokenResponse.status === 200) {
        this.authorized = authTokenResponse.data.access_token;
      }
    }
  }
  public async getPing() {
    return JSON.stringify(this.options);
  }

  public async getPlaylists(id: string) {
    await this.authorize();
    const auth = this.authorized;
    return (
      await lastValueFrom(
        this.httpService.get(`https://api.spotify.com/v1/playlists/${id}/tracks`, {
          headers: {
            Authorization: `Bearer ${this.authorized}`,
          },
        }),
      )
    ).data;
  }
}
