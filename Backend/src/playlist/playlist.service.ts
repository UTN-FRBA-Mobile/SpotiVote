import { Injectable } from '@nestjs/common';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { InjectModel } from '@nestjs/mongoose';
import { Playlist } from './schemas/playlist.schema';
import { Model } from 'mongoose';
import { SpotifyService } from '@app/spotify';
import { Song } from './schemas/song.schema';
import { PlaylistGateway } from './playlist.gateway';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectModel(Playlist.name) private playlistModel: Model<Playlist>,
    @InjectModel(Song.name) private songModel: Model<Song>,
    private spotifyService: SpotifyService,
    private playlistGateway: PlaylistGateway,
  ) {}

  async create(createCatDto: CreatePlaylistDto): Promise<Playlist> {
    const createdPlaylist = new this.playlistModel(createCatDto);
    return createdPlaylist.save();
  }

  async findAll() {
    return;
  }

  public async findById(id: string) {
    const playlistResponse = await this.spotifyService.getPlaylists(id);
    
    const images = playlistResponse.tracks.items[0].track.album.images;

    const playlist = await this.playlistModel.create({
      name: playlistResponse.name,
      id,
      description: playlistResponse.description,
      albumImageUri: (images && images.length > 0) ? images[0].url : "",
    });
    const mapSongs = playlistResponse.tracks.items.map(({ track }) => ({
      track: track.name,
      album: track.album.name,
      artist: track.artists[0].name,
      likes: 0,
      playlistId: id,
    }));

    await this.songModel.insertMany(mapSongs);

    const songs = await this.songModel.find({ playlistId: id }).exec();
    return { playlist, songs };
  }

  public async modifyLikes(
    playlistId: string,
    songId: string,
    increment: number,
  ) {
    const incrementLike = await this.songModel.findOneAndUpdate(
      { _id: songId, playlistId },
      { $inc: { likes: increment } },
      { new: true },
    );

    await this.playlistGateway.sendMessage(songId, incrementLike.likes);
    return incrementLike;
  }
}
