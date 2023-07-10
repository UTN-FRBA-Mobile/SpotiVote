import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { DeviceToken } from './schemas/device-token.schema';
import { Model } from 'mongoose';
import { CreateDeviceTokenDto } from './dto/create-device-token.dto';

@Injectable()
export class DeviceTokenService {
  constructor(
    @InjectModel(DeviceToken.name)
    private readonly deviceTokenModel: Model<DeviceToken>,
  ) {}

  async getAllDeviceTokens(): Promise<DeviceToken[]> {
    return this.deviceTokenModel.find().exec();
  }
  
  async createDeviceToken(
    createTokenDto: CreateDeviceTokenDto,
  ): Promise<DeviceToken> {
    const { userId, deviceToken } = createTokenDto;
    const existingDeviceToken = await this.deviceTokenModel.findOne({
      userId,
      deviceToken,
    });

    if (!existingDeviceToken) {
      const createdToken = new this.deviceTokenModel(createTokenDto);
      return createdToken.save();
    }
  }
}
