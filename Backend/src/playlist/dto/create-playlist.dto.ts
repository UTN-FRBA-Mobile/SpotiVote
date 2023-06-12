import { IsNumber, IsString } from 'class-validator';

export class CreatePlaylistDto {
  @IsString()
  name: string;

  @IsNumber()
  likes: number;
}
