import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { CreateRoomDto } from './dto/create-room.dto';
import { RoomService } from './room.service';
import { AddCandidateDto } from './dto/add-candidate.dto';
import { VoteSongDto } from './dto/vote-song.dto';

@Controller('room')
export class RoomController {
  constructor(private readonly roomService: RoomService) {}

  @Post()
  create(@Body() createRoomDto: CreateRoomDto) {
    return this.roomService.create(createRoomDto);
  }

  @Get()
  findAll() {
    return this.roomService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.roomService.findOne(id);
  }

  @Post(':id/candidates')
  addCandidate(
    @Param('id') id: string,
    @Body() addCandidateDto: AddCandidateDto,
  ) {
    return this.roomService.addCandidate(id, addCandidateDto);
  }

  @Patch(':id/votes')
  vote(@Param('id') id: string, @Body() voteSongDto: VoteSongDto) {
    return this.roomService.voteSong(id, voteSongDto);
  }
}
