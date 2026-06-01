package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class LoginResponse(
    val idUsuario: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val email: String
) : Serializable