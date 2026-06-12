package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class MascotaList(
    val idMascota: Int,
    val nombre: String,
    val raza: String?,
    val sexo: String,
    val estadoAdopcion: String,
    val tipoMascota: String,
    val urlFotoPrincipal: String? = null
) : Serializable