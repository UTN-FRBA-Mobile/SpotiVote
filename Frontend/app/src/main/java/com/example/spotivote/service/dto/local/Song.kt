package com.example.spotivote.service.dto.local

data class Song(
    val _id: String,
    val id: String,
    val trackName: String,
    val artist: String,
    val album: String,
    val likes: Int,
    val playlistId: String,
    val addedById: String,
    val __v: Int
)