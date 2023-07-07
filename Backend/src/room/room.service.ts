import { SpotifyService } from '@app/spotify';
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { CreateRoomDto } from './dto/create-room.dto';
import { UpdateRoomDto } from './dto/update-room.dto';
import { Room } from './schemas/room.schema';
import { AddCandidateDto } from './dto/add-candidate.dto';
import { VoteSongDto } from './dto/vote-song.dto';

@Injectable()
export class RoomService {
  constructor(
    @InjectModel(Room.name) private roomModel: Model<Room>,
    private spotifyService: SpotifyService,
  ) {}

  create(createRoomDto: CreateRoomDto) {
    // TODO: crear playlist en spotify con el nombre de la sala usar el id de esa playlist para crear la sala
    // TODO: Buscar 3 canciones de la playlist base y agregarlas a candidates
    // TODO: Agarrar 1 canción random y empezar a reproducirla

    const { name, basePlaylistId, deviceId, owner } = createRoomDto;
    const newRoom = this.roomModel.create({
      name,
      deviceId,
      basePlaylistId,
      playlistId: '',
      users: [owner],
      candidates: [],
    });

    return newRoom;
  }

  async findAll() {
    return await this.roomModel.find();
  }

  async findOne(id: string) {
    return await this.roomModel.findById(id);
  }

  async closeVotingAndAddToPlaylist(roomId: string) {
    const room = await this.roomModel.findById(roomId).exec();
    if (!room) {
      throw new Error('Room not found');
    }

    if (room.candidates.length === 0) {
      throw new Error('No candidates available');
    }

    // Obtener el candidato más votado
    const sortedCandidates = [...room.candidates].sort(
      (a, b) => b.votes.length - a.votes.length,
    );
    const mostVotedCandidate = sortedCandidates[0];
    const { addedBy, track } = mostVotedCandidate;

    // TODO: Agregar el candidato más votado a la room.playlistId de spotify
    // la canción la tiene que agregar el usuario que la agregó a la lista de candidatos

    // TODO: sumarle puntos al que propuso la canción que ganó

    // TODO: 3 nuevos candidates random del basePlaylistId
    // Restablecer la lista de candidatos y votos
    room.candidates = [];

    // TODO: disparar evento de que terminó la votación

    return await room.save();
  }

  async addCandidate(roomId: string, addCandidateDto: AddCandidateDto) {
    // TODO: validar si el usuario tiene puntos para agregar una canción

    const { user, track } = addCandidateDto;
    const room = await this.roomModel.findByIdAndUpdate(roomId, {
      $push: { candidates: { user, track, votes: [] } },
    });

    // TODO: restar puntos al usuario
    // TODO: disparar evento de que se agregó un candidato
  }

  async voteSong(roomId: string, voteSongDto: VoteSongDto): Promise<Room> {
    const room = await this.roomModel.findById(roomId).exec();
    if (!room) {
      throw new Error('Room not found');
    }

    const { user, track } = voteSongDto;
    const candidateIndex = room.candidates.findIndex((c) => c.track === track);
    if (candidateIndex === -1) {
      throw new Error('Candidate song not found');
    }

    const candidate = room.candidates[candidateIndex];
    const updatedVotes = [...candidate.votes];

    const userVoteIndex = updatedVotes.indexOf(user);
    if (userVoteIndex !== -1) {
      // Si el usuario ya ha votado, reasignar el voto quitando el voto anterior
      updatedVotes.splice(userVoteIndex, 1);
    }

    const updatedCandidate = {
      ...candidate,
      votes: [...updatedVotes, user],
    };

    const updatedCandidates = [
      ...room.candidates.slice(0, candidateIndex),
      updatedCandidate,
      ...room.candidates.slice(candidateIndex + 1),
    ];

    const updatedRoom = {
      ...room.toObject(),
      candidates: updatedCandidates,
    };

    // TODO: disparar evento de que cambiaron los votos

    return await this.roomModel.findByIdAndUpdate(roomId, updatedRoom, {
      new: true,
    });
  }
}
