package com.example.spotivote.service.dto.local

data class CreateRoomRequest(
    val name: String,
    val deviceId: String,
    val basePlaylistId: String,
    val owner: String,
    val accessToken: String
)