import { HttpService } from '@nestjs/axios';
import { Injectable } from '@nestjs/common';
import { lastValueFrom } from 'rxjs';
import {
  CreatedPlaylist,
  Playlist,
  Track,
} from './contracts/playlist-response';

@Injectable()
export class SpotifyService {
  constructor(private httpService: HttpService) {}

  public async getUser(userId: string, accessToken: string) {
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
      profileImage:
        response.data.images.length > 0 ? response.data.images[0].url : '',
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
