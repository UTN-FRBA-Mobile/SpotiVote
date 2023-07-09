import { Module } from '@nestjs/common';
import { DeviceTokenController } from './device-token.controller';
import { DeviceTokenService } from './device-token.service';
import { MongooseModule } from '@nestjs/mongoose';
import { DeviceToken, DeviceTokenSchema } from './schemas/device-token.schema';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: DeviceToken.name, schema: DeviceTokenSchema },
    ]),
  ],
  controllers: [DeviceTokenController],
  providers: [DeviceTokenService],
})
export class DeviceTokenModule {}
