import { Injectable } from '@nestjs/common';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { InjectModel } from '@nestjs/mongoose';
import { Playlist } from './schemas/playlist.schema';
import { Model } from 'mongoose';
import { SpotifyService } from '@app/spotify';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectModel(Playlist.name) private playlistModel: Model<Playlist>,
    private spotifyService: SpotifyService,
  ) {}

  async create(createCatDto: CreatePlaylistDto): Promise<Playlist> {
    const createdPlaylist = new this.playlistModel(createCatDto);
    return createdPlaylist.save();
  }

  async findAll() {
    return;
  }

  public async findById(id: string) {
    const value = await this.spotifyService.getPlaylists(id);
    return value;
  }
}
