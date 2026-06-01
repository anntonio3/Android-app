package mx.edu.unpa.miandroid.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREF_NAME   = "sesion_adoptame"
    private const val KEY_ID      = "idUsuario"
    private const val KEY_NOMBRE  = "nombre"
    private const val KEY_EMAIL   = "email"
    private const val KEY_LOGGED  = "logged"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun guardar(ctx: Context, id: Int, nombre: String, email: String) {
        prefs(ctx).edit()
            .putInt(KEY_ID, id)
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_LOGGED, true)
            .apply()
    }

    fun isLoggedIn(ctx: Context) = prefs(ctx).getBoolean(KEY_LOGGED, false)

    fun getIdUsuario(ctx: Context) = prefs(ctx).getInt(KEY_ID, -1)

    fun getNombre(ctx: Context) = prefs(ctx).getString(KEY_NOMBRE, "") ?: ""

    fun getEmail(ctx: Context) = prefs(ctx).getString(KEY_EMAIL, "") ?: ""

    fun cerrarSesion(ctx: Context) = prefs(ctx).edit().clear().apply()
}