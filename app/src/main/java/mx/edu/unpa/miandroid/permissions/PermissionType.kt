package mx.edu.unpa.miandroid.permissions

import android.Manifest
enum class PermissionType(
    val permission: String
) {

    CAMERA(
        Manifest.permission.CAMERA
    ),

    LOCATION(
        Manifest.permission.ACCESS_FINE_LOCATION
    ),

    MICROPHONE(
        Manifest.permission.RECORD_AUDIO
    ),

    STORAGE_READ(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ),

    STORAGE_WRITE(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}