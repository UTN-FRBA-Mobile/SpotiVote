import { Prop, SchemaFactory, Schema } from '@nestjs/mongoose';
import mongoose, { HydratedDocument } from 'mongoose';
import { Playlist } from './playlist.schema';

export type Document = HydratedDocument<Song>;

@Schema()
export class Song {
  @Prop()
  _id: mongoose.Schema.Types.ObjectId;

  @Prop()
  artist: string;

  @Prop()
  track: string;

  @Prop()
  album: string;

  @Prop()
  likes: number;

  @Prop({
    required: true,
  })
  playlistId: string;
}

export const SongSchema = SchemaFactory.createForClass(Song);
