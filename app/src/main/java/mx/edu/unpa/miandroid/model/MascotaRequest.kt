package mx.edu.unpa.miandroid.model

data class MascotaRequest(
    val nombre: String,
    val idTipoMascota: Int,
    val raza: String,
    val sexo: String               // "Macho" o "Hembra"
)