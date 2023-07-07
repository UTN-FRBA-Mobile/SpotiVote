import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type Document = HydratedDocument<Playlist>;

@Schema()
export class Playlist {
  @Prop()
  id: string;

  @Prop()
  name: string;

  @Prop()
  description: string;

  @Prop()
  albumImageUri: string;
}

export const PlaylistSchema = SchemaFactory.createForClass(Playlist);
