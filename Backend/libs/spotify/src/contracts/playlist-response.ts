export interface Playlist {
  description: string;
  name: string;
  tracks: Tracks;
  albumImageUri: string;
}

export interface Tracks {
  items: Item[];
}

export interface Item {
  track: Track;
  added_by: AddedBy;
}

export interface Track {
  id: string;
  album: Album;
  artists: Album[];
  href: string;
  name: string;
}

export interface AddedBy {
  id: string;
}

export interface User {
  id: string;
  username: string;
  displayName: string;
  profileImage: string;
}

export interface Album {
  name: string;
  images: AlbumImage[];
}

export interface AlbumImage {
  url: string;
}

export type CreatedPlaylist = {
  type: 'playlist';
  collaborative: boolean;
  description: string;
  id: string;
  name: string;
  uri: string;
};