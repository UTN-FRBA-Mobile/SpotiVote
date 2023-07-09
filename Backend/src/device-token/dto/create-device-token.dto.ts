import { IsString } from 'class-validator';

export class CreateDeviceTokenDto {
  @IsString()
  deviceToken: string;

  @IsString()
  userId: string;
}
