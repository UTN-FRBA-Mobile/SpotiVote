import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Patch,
  Query,
} from '@nestjs/common';
import { CreatePlaylistDto } from './dto/create-playlist.dto';
import { PlaylistService } from './playlist.service';

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
  findById(
    @Param('id') id: string,
    @Param('accessToken')
    accessToken = 'BQAp4tVDoyKMCbmq3h47LQE9ssF-9Od35L0tvdo5RSTuTBDDfbNmPGEILjt0QrE5oyZl6qr2eweuvxYKng3uZsrH2dbefvZTzJhNMTFG1X03lNYOnwDr4YQCUFUZT5hGJ2Xk28hNbfqco7TcKggPWLDOhvpCjiI5ewp6lpFtG6w2Rp57ESrvCBX7bnziOwg4sz4irbZh5ZMsyndNgOOi_h54jV9T6v-beANxsTuBIX69Eop7PpZlfupefMc8VvepN_X3KXuX2IY01GX4rA',
  ) {
    return this.playlistService.findById(id, accessToken);
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
