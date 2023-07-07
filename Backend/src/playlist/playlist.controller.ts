import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Patch,
  Query,
} from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { CreateDeviceTokenDto } from './dto/create-device-token.dto';

@Controller('playlist')
export class PlaylistController {
  constructor(private readonly playlistService: PlaylistService) { }

  @Post()
  create(@Body() createPlaylistDto: CreatePlaylistDto) {
    return this.playlistService.create(createPlaylistDto);
  }

  @Post('/deviceToken')
  createDeviceToken(@Body() createDeviceTokenDto: CreateDeviceTokenDto) {
    return this.playlistService.createDeviceToken(createDeviceTokenDto);
  }

  @Get()
  findAll() {
    return this.playlistService.findAll();
  }

  @Get(':id')
  findById(@Param('id') id: string) {
    return this.playlistService.findById(id);
  }

  @Get('/:playlistId/song/:songId/')
  findSongById(
    @Param('playlistId') playlistId: string,
    @Param('songId') songId: string,
  ) {
    return this.playlistService.findSongById(playlistId, songId);
  }

  @Patch('/:playlistId/song/:songId/thumbs-up')
  increaseLikes(
    @Param('playlistId') playlistId: string,
    @Param('songId') songId: string,
  ) {
    return this.playlistService.modifyLikes(playlistId, songId, 1);
  }

  @Patch('/:playlistId/song/:songId/thumbs-down')
  decreaseLikes(
    @Param('playlistId') playlistId: string,
    @Param('songId') songId: string,
  ) {
    return this.playlistService.modifyLikes(playlistId, songId, -1);
  }
}
