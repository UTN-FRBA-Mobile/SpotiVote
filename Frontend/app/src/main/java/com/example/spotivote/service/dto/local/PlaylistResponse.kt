package com.example.spotivote.service.dto.local


data class PlaylistResponse(
    val playlist: Playlist,
    val songs: List<Song>
)