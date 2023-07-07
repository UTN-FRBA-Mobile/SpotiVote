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

@Schema()
export class Room extends Document {
  @Prop({ required: true })
  name: string;

  @Prop({ required: true })
  playlistId: string;

  @Prop({ required: true })
  deviceId: string;

  @Prop({ required: true })
  basePlaylistId: string;

  @Prop({ type: [{ type: String }] })
  users: string[];

  @Prop({ type: [Candidate] })
  candidates: ICandidate[];
}

export const RoomSchema = SchemaFactory.createForClass(Room);
