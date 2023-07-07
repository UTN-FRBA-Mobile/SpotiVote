package com.example.spotivote.service.firebase

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object MyPreferences {
    private const val PREF_NAME = "PREF_NAME"
    private const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
    private const val HAS_NOTIFICATION_PERMISSION = "HAS_NOTIFICATION_PERMISSION"

    fun getFirebaseToken(context: Context): String? {
        return getPreferences(context).getString(FIREBASE_TOKEN, null)
    }

    fun setFirebaseToken(context: Context, token: String) {
        val editor = getPreferencesEditor(context)
        editor.putString(FIREBASE_TOKEN, token)
        editor.apply()
    }


    fun getNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setNotificationPermission(
                context,
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else setNotificationPermission(context, true)
        return getPreferences(context).getBoolean(HAS_NOTIFICATION_PERMISSION, false)
    }

    fun setNotificationPermission(context: Context, hasNotificationPermission: Boolean) {
        val editor = getPreferencesEditor(context)
        editor.putBoolean(HAS_NOTIFICATION_PERMISSION, hasNotificationPermission)
        editor.apply()
    }

    private fun getPreferencesEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}