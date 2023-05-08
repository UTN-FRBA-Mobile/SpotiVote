package com.example.spotivote.service.dto

data class TrackItem(
    val album: TrackItemAlbum, val name: String,
    val id: String, val artists: List<TrackItemAlbumArtist>
)