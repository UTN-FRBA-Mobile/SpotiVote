import { SpotifyService } from '@app/spotify';
import { HttpService } from '@nestjs/axios';
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { lastValueFrom } from 'rxjs';
import { CreatedPlaylist, SpotifyPlaylist } from 'src/types';
import { randomFromList, shuffle } from 'src/utils';
import { AddCandidateDto } from './dto/add-candidate.dto';
import { CreateRoomDto } from './dto/create-room.dto';
import { VoteTrackDto } from './dto/vote-track.dto';
import { ICandidate, Room } from './schemas/room.schema';

@Injectable()
export class RoomService {
  constructor(
    @InjectModel(Room.name) private roomModel: Model<Room>,
    private spotifyService: SpotifyService,
    private httpService: HttpService,
  ) {}

  async create(createRoomDto: CreateRoomDto) {
    const { name, basePlaylistId, deviceId, owner, accessToken } =
      createRoomDto;

    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    };

    const [playlist, basePlaylist] = await Promise.all([
      lastValueFrom(
        this.httpService.post<CreatedPlaylist>(
          `https://api.spotify.com/v1/users/${owner}/playlists`,
          {
            name,
            public: false,
            collaborative: true,
            description: 'Playlist created by SpotiVote',
          },
          config,
        ),
      ),
      lastValueFrom(
        this.httpService.get<SpotifyPlaylist>(
          `https://api.spotify.com/v1/playlists/${basePlaylistId}`,
          config,
        ),
      ),
    ]);

    const firstTrack = randomFromList(basePlaylist.data.tracks.items);

    const pool = basePlaylist.data.tracks.items.filter(
      (track) => track.track.id !== firstTrack.track.id,
    );

    const randomTracks: ICandidate[] = shuffle(pool)
      .slice(0, 3)
      .map((track) => {
        return {
          addedBy: owner,
          track: track.track.id,
          votes: [],
        };
      });

    const addFirstTrack = await lastValueFrom(
      this.httpService.post(
        `https://api.spotify.com/v1/playlists/${playlist.data.id}/tracks`,
        {
          uris: [`spotify:track:${firstTrack.track.id}`],
        },
        config,
      ),
    );

    const transferPlayback = await lastValueFrom(
      this.httpService.put(
        `https://api.spotify.com/v1/me/player`,
        {
          device_ids: [deviceId],
          play: false,
        },
        config,
      ),
    );

    const playFirstSong = await lastValueFrom(
      this.httpService.put(
        `https://api.spotify.com/v1/me/player/play`,
        {
          context_uri: `spotify:playlist:${playlist.data.id}`,
        },
        config,
      ),
    );

    const newRoom = await this.roomModel.create({
      name,
      deviceId,
      basePlaylistId,
      playlistId: playlist.data.id,
      users: [{ id: owner, points: 0 }],
      candidates: randomTracks,
      currentTrack: {
        addedBy: owner,
        track: firstTrack.track.id,
        votes: [] as string[],
      },
    });

    return newRoom;
  }

  async findAll() {
    return await this.roomModel.find().exec();
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

    room.users.find((u) => u.id === addedBy).points += 10;
    room.currentTrack = mostVotedCandidate;

    // TODO: 3 nuevos candidates random del basePlaylistId
    // Restablecer la lista de candidatos y votos
    room.candidates = [];

    // TODO: disparar evento de que terminó la votación
    return await room.save();
  }

  async addCandidate(roomId: string, addCandidateDto: AddCandidateDto) {
    const room = await this.roomModel.findById(roomId).exec();
    if (!room) {
      throw new Error('Room not found');
    }
    const { user: userId, track } = addCandidateDto;

    const user = room.users.find((u) => u.id === userId);

    if (user.points >= 3) {
      user.points -= 3;
      room.candidates.push({ addedBy: userId, track, votes: [userId] });
    }

    return await room.save();
    // TODO: disparar evento de que se agregó un candidato
  }

  async voteTrack(roomId: string, voteTrackDto: VoteTrackDto): Promise<Room> {
    const { user, track } = voteTrackDto;

    const room = await this.roomModel.findById(roomId).populate('candidates');

    room.candidates.forEach((candidate: ICandidate) => {
      const voteIndex = candidate.votes.indexOf(user);
      if (voteIndex !== -1) {
        candidate.votes.splice(voteIndex, 1);
      }
    });

    const candidate = room.candidates.find(
      (candidate: ICandidate) => candidate.track === track,
    );

    candidate.votes.push(user);

    return await room.save();
  }
}
