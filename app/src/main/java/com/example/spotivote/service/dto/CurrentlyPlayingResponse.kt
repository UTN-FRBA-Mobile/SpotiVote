package com.example.spotivote.service.dto

data class CurrentlyPlayingResponse(
    val context: CurrentlyPlayingContext,
    val item: TrackItem,
)