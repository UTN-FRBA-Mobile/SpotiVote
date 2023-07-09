import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

const Track = {
  id: String,
  album: {
    name: String,
    images: [{ url: String }],
  },
  artists: [{ name: String }],
  name: String,
};

export interface ITrack {
  id: string;
  album: {
    name: string;
    images: { url: string }[];
  };
  artists: { name: string }[];
  name: string;
}

const Candidate = {
  addedBy: String,
  track: Track,
  votes: [{ type: String }],
};

export interface ICandidate {
  addedBy: string;
  track: ITrack;
  votes: string[];
}

const User = {
  id: String,
  accessToken: String,
  points: Number,
};

export interface IUser {
  id: string;
  accessToken: string;
  points: number;
}

@Schema()
export class Room extends Document {
  @Prop({ required: true })
  name: string;

  @Prop({ required: true })
  owner: string;

  @Prop({ required: true })
  playlistId: string;

  @Prop({ required: true })
  deviceId: string;

  @Prop({ required: true })
  basePlaylistId: string;

  @Prop({ type: [User] })
  users: IUser[];

  @Prop({ type: [Candidate] })
  candidates: ICandidate[];

  @Prop({ type: Candidate })
  currentTrack: ICandidate;
}

export const RoomSchema = SchemaFactory.createForClass(Room);
