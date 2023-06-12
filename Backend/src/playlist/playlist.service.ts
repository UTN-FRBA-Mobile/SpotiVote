import { Injectable } from '@nestjs/common';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { UpdatePlaylistDto } from './dto/update-playlist.dto';
import { InjectModel } from '@nestjs/mongoose';
import { Playlist } from './schemas/playlist.schema';
import { Model } from 'mongoose';

@Injectable()
export class PlaylistService {
  constructor(
    @InjectModel(Playlist.name) private playlistModel: Model<Playlist>,
  ) {}

  async create(createCatDto: CreatePlaylistDto): Promise<Playlist> {
    const createdPlaylist = new this.playlistModel(createCatDto);
    return createdPlaylist.save();
  }

  async findAll(): Promise<Playlist[]> {
    return this.playlistModel.find().exec();
  }
}
