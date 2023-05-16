package com.example.spotivote.service.dto

data class UserResponse(
    val id: String,
    val display_name: String,
    val images: List<Image>
)