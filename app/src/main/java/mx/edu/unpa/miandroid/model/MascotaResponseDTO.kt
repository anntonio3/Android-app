package mx.edu.unpa.miandroid.model

data class MascotaResponseDTO(
    val idMascota: Int,
    val nombre: String,
    val tipoMascotaDescripcion: String?,
    val estadoAdopcion: String?
)