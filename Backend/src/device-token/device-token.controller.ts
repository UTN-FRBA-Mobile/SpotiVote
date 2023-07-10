import { Body, Controller, Get, Post } from '@nestjs/common';
import { DeviceTokenService } from './device-token.service';
import { CreateDeviceTokenDto } from './dto/create-device-token.dto';

@Controller('device-token')
export class DeviceTokenController {
  constructor(private readonly deviceTokenService: DeviceTokenService) {}

  @Get()
  getAllDeviceTokens() {
    return this.deviceTokenService.getAllDeviceTokens();
  }
  
  @Post()
  createDeviceToken(@Body() createDeviceTokenDto: CreateDeviceTokenDto) {
    return this.deviceTokenService.createDeviceToken(createDeviceTokenDto);
  }
}
