import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type Document = HydratedDocument<DeviceToken>;

@Schema()
export class DeviceToken {
  @Prop()
  id: string;

  @Prop()
  deviceToken: string;

  @Prop()
  userId: string;
}

export const DeviceTokenSchema = SchemaFactory.createForClass(DeviceToken);
