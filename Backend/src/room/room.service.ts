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
import { ICandidate, IUser, Room } from './schemas/room.schema';

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
      users: [{ id: owner, points: 10, accessToken }],
      owner: owner,
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
    return await this.roomModel.findById(id).exec();
  }

  async closeVotingAndAddToPlaylist(roomId: string) {
    const room = await this.roomModel.findById(roomId).exec();

    if (room === null) {
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
    const { addedBy, track: winnerTrack } = mostVotedCandidate;
    const winnerUser = room.users.find((u) => u.id === addedBy);

    const ownerUser = room.users.find((u) => u.id === room.owner);

    const config = {
      headers: {
        Authorization: `Bearer ${winnerUser.accessToken}`,
      },
    };
    const basePlaylist = await lastValueFrom(
      this.httpService.get<SpotifyPlaylist>(
        `https://api.spotify.com/v1/playlists/${room.basePlaylistId}`,
        config,
      ),
    );
    const pool = basePlaylist.data.tracks.items.filter(
      (track) => track.track.id !== winnerTrack,
    );

    const randomTracks: ICandidate[] = shuffle(pool)
      .slice(0, 3)
      .map((track) => {
        return {
          addedBy: room.owner,
          track: track.track.id,
          votes: [],
        };
      });

    winnerUser.points += 10;

    room.currentTrack = mostVotedCandidate;

    const addFirstTrack = await lastValueFrom(
      this.httpService.post(
        `https://api.spotify.com/v1/playlists/${room.playlistId}/tracks`,
        {
          uris: [`spotify:track:${winnerTrack}`],
        },
        config,
      ),
    );

    const transferPlayback = await lastValueFrom(
      this.httpService.put(
        `https://api.spotify.com/v1/me/player`,
        {
          device_ids: [room.deviceId],
          play: false,
        },
        {
          headers: {
            Authorization: `Bearer ${ownerUser.accessToken}`,
          },
        },
      ),
    );

    const playNextSong = await lastValueFrom(
      this.httpService.post(
        `https://api.spotify.com/v1/me/player/next`,
        {},
        {
          headers: {
            Authorization: `Bearer ${ownerUser.accessToken}`,
          },
        },
      ),
    );

    room.candidates = randomTracks;

    // TODO: disparar evento de que terminó la votación
    return await room.save();
  }

  async addCandidate(
    roomId: string,
    addCandidateDto: AddCandidateDto,
  ): Promise<Room> {
    const { userId, trackId } = addCandidateDto;

    // Encuentra la sala por el ID y asegúrate de cargar las propiedades de 'users' y 'candidates'
    const room = await this.roomModel
      .findById(roomId)
      .populate('users')
      .populate('candidates');

    // Verifica si el usuario tiene suficientes puntos para agregar una canción candidata
    const user = room.users.find((user: IUser) => user.id === userId);
    if (!user || user.points < 3) {
      throw new Error('Not enough points');
    }

    // Resta 3 puntos al usuario
    user.points -= 3;

    // Agrega la nueva canción candidata al pool
    const newCandidate: ICandidate = {
      addedBy: userId,
      track: trackId,
      votes: [],
    };

    room.candidates.push(newCandidate);

    // Guarda los cambios en la base de datos
    await room.save();
    return await this.voteTrack(roomId, { userId, trackId });
  }

  async voteTrack(roomId: string, voteTrackDto: VoteTrackDto): Promise<Room> {
    const { userId, trackId } = voteTrackDto;

    const room = await this.roomModel.findById(roomId).populate('candidates');

    room.candidates.forEach((candidate: ICandidate) => {
      const voteIndex = candidate.votes.indexOf(userId);
      if (voteIndex !== -1) {
        candidate.votes.splice(voteIndex, 1);
      }
    });

    const candidate = room.candidates.find(
      (candidate: ICandidate) => candidate.track === trackId,
    );

    candidate.votes.push(userId);

    return await room.save();
  }
}
