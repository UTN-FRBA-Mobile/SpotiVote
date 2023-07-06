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
  addedBy: AddedBy;
}

export interface Track {
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
