import { SpotifyService } from '@app/spotify';
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { CreateDeviceTokenDto } from './dto/create-device-token.dto';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { DeviceToken } from './schemas/device-token.schema';
import { Playlist } from './schemas/playlist.schema';
import { Song } from './schemas/song.schema';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectModel(Playlist.name) private playlistModel: Model<Playlist>,
    @InjectModel(Song.name) private songModel: Model<Song>,
    @InjectModel(DeviceToken.name) private deviceTokenModel: Model<DeviceToken>,
    private spotifyService: SpotifyService,
  ) { }

  async create(createCatDto: CreatePlaylistDto): Promise<Playlist> {
    const createdPlaylist = new this.playlistModel(createCatDto);
    return createdPlaylist.save();
  }

  async createDeviceToken(createTokenDto: CreateDeviceTokenDto): Promise<DeviceToken> {
    const { userId, deviceToken } = createTokenDto;
    const existingDeviceToken = await this.deviceTokenModel.findOne({ userId, deviceToken });

    if (!existingDeviceToken) {
      const createdToken = new this.deviceTokenModel(createTokenDto);
      return createdToken.save();
    }
  }

  async findAll() {
    return;
  }

  public async findById(id: string) {
    const playlistResponse = await this.spotifyService.getPlaylists(id);

    const images = playlistResponse.tracks.items[0].track.album.images;

    let playlist = await this.playlistModel.findOne({ id });

    if (!playlist) {
      playlist = await this.playlistModel.create({
        name: playlistResponse.name,
        id,
        description: playlistResponse.description,
        albumImageUri: (images && images.length > 0) ? images[0].url : "",
      });
      const mapSongs = playlistResponse.tracks.items.map(({ track, added_by }) => ({
        id: track.id,
        trackName: track.name,
        album: track.album.name,
        artist: track.artists[0].name,
        likes: 0,
        playlistId: id,
        addedById: added_by.id,
      }));

      await this.songModel.insertMany(mapSongs);
    }

    const songs = await this.songModel.find({ playlistId: id }).exec();
    return { playlist, songs };
  }

  public async findSongById(playlistId: string, songId: string) {
    let song = await this.songModel.findOne({ playlistId, id: songId });
    return { song };
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

    return incrementLike;
  }
}
