import { HttpService } from '@nestjs/axios';
import { Inject, Injectable } from '@nestjs/common';
import { lastValueFrom } from 'rxjs';
import {
  CreatedPlaylist,
  Playlist,
  Track,
  User,
} from './contracts/playlist-response';
import { SPOTIFY_MODULE_OPTIONS_TOKEN } from './interfaces/spotify-module-definition';
import { SpotifyModuleOptions } from './interfaces/spotify-module-options';

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
        this.httpService.get<Playlist>(
          `https://api.spotify.com/v1/playlists/${id}`,
          {
            params: {
              fields:
                'name,description,tracks.items(track(id,name,href,album(name),artists(name),album(images)),added_by.id)',
            },
            headers: {
              Authorization: `Bearer ${this.authorized}`,
            },
          },
        ),
      )
    ).data;
  }

  public async getUser(userId: String, accessToken: string) {
    const response = await lastValueFrom(
      this.httpService.get<{
        id: string;
        display_name: string;
        images: { url: string }[];
      }>(`https://api.spotify.com/v1/users/${userId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      }),
    );

    return {
      id: response.data.id,
      displayName: response.data.display_name,
      profileImage: response.data.images[0].url,
    };
  }

  public async createPlaylist(
    userId: string,
    name: string,
    description: string,
    accessToken: string,
  ) {
    const response = await lastValueFrom(
      this.httpService.post<CreatedPlaylist>(
        `https://api.spotify.com/v1/users/${userId}/playlists`,
        {
          name,
          public: false,
          collaborative: true,
          description,
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }

  public async getPlaylist(playlistId: string, accessToken: string) {
    const response = await lastValueFrom(
      this.httpService.get<Playlist>(
        `https://api.spotify.com/v1/playlists/${playlistId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }

  public async addTrackToPlaylist(
    playlistId: string,
    trackId: string,
    accessToken: string,
  ) {
    const response = await lastValueFrom(
      this.httpService.post<{
        snapshot_id: string;
      }>(
        `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
        {
          uris: [`spotify:track:${trackId}`],
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }

  public async transferPlayback(deviceId: string, accessToken: string) {
    const response = await lastValueFrom(
      this.httpService.put(
        `https://api.spotify.com/v1/me/player`,
        {
          device_ids: [deviceId],
          play: false,
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }

  public async playTrackInPlaylist(
    playlistId: string,
    trackId: string,
    accessToken: string,
  ) {
    const response = await lastValueFrom(
      this.httpService.put(
        `https://api.spotify.com/v1/me/player/play`,
        {
          context_uri: `spotify:playlist:${playlistId}`,
          offset: {
            uri: `spotify:track:${trackId}`,
          },
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }

  public async getTrack(trackId: string, accessToken: string) {
    const response = await lastValueFrom(
      this.httpService.get<Track>(
        `https://api.spotify.com/v1/tracks/${trackId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      ),
    );

    return response.data;
  }
}
