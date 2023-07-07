import { Prop, SchemaFactory, Schema } from '@nestjs/mongoose';
import mongoose, { HydratedDocument } from 'mongoose';

export type Document = HydratedDocument<Song>;

@Schema()
export class Song {
  @Prop()
  _id: mongoose.Schema.Types.ObjectId;

  @Prop({ required: true })
  id: string;

  @Prop()
  artist: string;

  @Prop()
  trackName: string;

  @Prop()
  album: string;

  @Prop()
  likes: number;

  @Prop({ required: true })
  playlistId: string;

  @Prop()
  addedById: string;
}

export const SongSchema = SchemaFactory.createForClass(Song);
