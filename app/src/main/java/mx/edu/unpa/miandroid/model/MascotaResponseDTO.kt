package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class MascotaResponseDTO(
    val idMascota: Int,
    val nombre: String,
    val raza: String?,
    val sexo: String?,
    val tipoMascotaDescripcion: String?,
    val estadoAdopcion: String?
) : Serializable