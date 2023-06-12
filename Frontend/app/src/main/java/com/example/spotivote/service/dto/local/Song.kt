package com.example.spotivote.service.dto.local

data class Song(
    val _id: String,
    val artist: String,
    val track: String,
    val album: String,
    val likes: Int,
    val playlistId: String,
    val __v: Int
)