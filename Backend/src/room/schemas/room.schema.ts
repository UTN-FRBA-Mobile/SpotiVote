import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

const Candidate = {
  addedBy: String,
  track: String,
  votes: [{ type: String }],
};

export interface ICandidate {
  addedBy: string;
  track: string;
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
