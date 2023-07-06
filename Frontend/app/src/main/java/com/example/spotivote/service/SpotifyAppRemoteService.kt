package com.example.spotivote.service

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

val CLIENT_ID = "4240d4ecfccb4c6eaa9f064ead594324"
val REDIRECT_URI = "com.example.spotivote://callback"
var spotifyAppRemote: SpotifyAppRemote? = null

val connectionParams = ConnectionParams.Builder(CLIENT_ID)
    .setRedirectUri(REDIRECT_URI)
    .showAuthView(true)
    .build()

fun connectSpotifyAppRemote(context: Context) {
    SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
        override fun onConnected(appRemote: SpotifyAppRemote) {
            spotifyAppRemote = appRemote
            Log.d(TAG, "SpotifyAppRemote Connected!")
        }

        override fun onFailure(throwable: Throwable) {
            Log.e(TAG, throwable.message, throwable)
        }
    })
}
