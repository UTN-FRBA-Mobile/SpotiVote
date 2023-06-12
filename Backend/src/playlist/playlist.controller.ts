import { Controller, Get, Post, Body, Param } from '@nestjs/common';
import { PlaylistService } from './playlist.service';
import { CreatePlaylistDto } from './dto/create-playlist.dto';

@Controller('playlist')
export class PlaylistController {
  constructor(private readonly playlistService: PlaylistService) {}

  @Post()
  create(@Body() createPlaylistDto: CreatePlaylistDto) {
    return this.playlistService.create(createPlaylistDto);
  }

  @Get()
  findAll() {
    return this.playlistService.findAll();
  }

  @Get(':id')
  findById(@Param('id') id: string) {
    return this.playlistService.findById(id);
  }
}
