import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type Document = HydratedDocument<Playlist>;

@Schema()
export class Playlist {
  @Prop()
  name: string;

  @Prop()
  likes: number;
}

export const PlaylistSchema = SchemaFactory.createForClass(Playlist);
