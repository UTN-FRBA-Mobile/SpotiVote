import { Body, Controller, Get, Param, Patch, Post } from '@nestjs/common';
import { AddCandidateDto } from './dto/add-candidate.dto';
import { CreateRoomDto } from './dto/create-room.dto';
import { VoteTrackDto } from './dto/vote-track.dto';
import { RoomService } from './room.service';

@Controller('rooms')
export class RoomController {
  constructor(private readonly roomService: RoomService) {}

  // crear una sala
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

  @Patch(':id/poll')
  endPoll(@Param('id') id: string) {
    return this.roomService.closeVotingAndAddToPlaylist(id);
  }

  @Patch(':id/votes')
  vote(@Param('id') id: string, @Body() voteTrackDto: VoteTrackDto) {
    return this.roomService.voteTrack(id, voteTrackDto);
  }
}
