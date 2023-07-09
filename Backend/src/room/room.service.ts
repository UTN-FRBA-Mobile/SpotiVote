import { SpotifyService } from '@app/spotify';
import { HttpService } from '@nestjs/axios';
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
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

    const [playlist, basePlaylist] = await Promise.all([
      this.spotifyService.createPlaylist(owner, name, '', accessToken),
      this.spotifyService.getPlaylist(basePlaylistId, accessToken),
    ]);

    const firstTrack = randomFromList(basePlaylist.tracks.items);

    const pool = basePlaylist.tracks.items.filter(
      (track) => track.track.id !== firstTrack.track.id,
    );

    const randomTracks: ICandidate[] = shuffle(pool)
      .slice(0, 3)
      .map((track) => {
        return {
          addedBy: owner,
          track: track.track,
          votes: [],
        };
      });

    const addFirstTrack = await this.spotifyService.addTrackToPlaylist(
      playlist.id,
      firstTrack.track.id,
      accessToken,
    );

    const transferPlayback = await this.spotifyService
      .transferPlayback(deviceId, accessToken)
      .catch((e) => {
        console.log('Error transferPlayback');
        console.log(e);
      });

    const playFirstSong = await this.spotifyService
      .playTrackInPlaylist(playlist.id, firstTrack.track.id, accessToken)
      .catch((e) => {
        console.log('Error playFirstSong');
        console.log(e);
      });

    const newRoom = await this.roomModel.create({
      name,
      deviceId,
      basePlaylistId,
      playlistId: playlist.id,
      users: [{ id: owner, points: 10, accessToken }],
      owner: owner,
      candidates: randomTracks,
      currentTrack: {
        addedBy: owner,
        track: firstTrack.track,
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

    // Obtener el candidato más votado
    const sortedCandidates = [...room.candidates].sort(
      (a, b) => b.votes.length - a.votes.length,
    );
    const mostVotedCandidate = sortedCandidates[0];
    const { addedBy, track: winnerTrack } = mostVotedCandidate;
    const winnerUser = room.users.find((u) => u.id === addedBy);

    const ownerUser = room.users.find((u) => u.id === room.owner);

    const basePlaylist = await this.spotifyService.getPlaylist(
      room.basePlaylistId,
      ownerUser.accessToken,
    );
    const pool = basePlaylist.tracks.items.filter(
      (track) => track.track.id !== winnerTrack.id,
    );

    const randomTracks: ICandidate[] = shuffle(pool)
      .slice(0, 3)
      .map((track) => {
        return {
          addedBy: room.owner,
          track: track.track,
          votes: [],
        };
      });

    winnerUser.points += 10;

    room.currentTrack = mostVotedCandidate;

    const addWinnerTrack = await this.spotifyService.addTrackToPlaylist(
      room.playlistId,
      winnerTrack.id,
      winnerUser.accessToken,
    );

    const playWinnerSong = await this.spotifyService.playTrackInPlaylist(
      room.playlistId,
      winnerTrack.id,
      ownerUser.accessToken,
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

    const track = await this.spotifyService.getTrack(trackId, user.accessToken);

    // Agrega la nueva canción candidata al pool
    const newCandidate: ICandidate = {
      addedBy: userId,
      track,
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
      (candidate: ICandidate) => candidate.track.id === trackId,
    );

    candidate.votes.push(userId);

    return await room.save();
  }
}
