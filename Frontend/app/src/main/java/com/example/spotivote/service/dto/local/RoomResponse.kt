package com.example.spotivote.service.dto.local


data class AlbumImage(val url: String)
data class Artist(val name: String)
data class Album(val name: String, val images: List<AlbumImage>)
data class CandidateTrack(
    val id: String,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
)

data class CandidateAddedBy(
    val id: String,
    val displayName: String,
    val profileImage: String,
)

data class UserInRoom(
    val id: String, val points: Number
)

data class Candidate(
    val addedBy: CandidateAddedBy,
    val track: CandidateTrack,
    val votes: List<String>,
)

data class RoomResponse(
    val name: String,
    val owner: String,
    val playlistId: String,
    val deviceId: String,
    val _id: String,
    val basePlaylistId: String,
    val candidates: List<Candidate>,
    val currentTrack: Candidate,
    val users: List<UserInRoom>
)
