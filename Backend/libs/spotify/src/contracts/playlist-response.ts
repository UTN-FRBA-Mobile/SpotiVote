export interface Playlist {
  description: string;
  name: string;
  tracks: Tracks;
}

export interface Tracks {
  items: Item[];
}

export interface Item {
  track: Track;
}

export interface Track {
  album: Album;
  artists: Album[];
  href: string;
  name: string;
}

export interface Album {
  name: string;
}
