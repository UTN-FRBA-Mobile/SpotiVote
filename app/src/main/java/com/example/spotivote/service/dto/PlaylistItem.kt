package com.example.spotivote.service.dto

data class PlaylistItem(
    val name: String,
    val href: String,
    val tracks: PlaylistItemTrack,
    val images: List<Image>
)