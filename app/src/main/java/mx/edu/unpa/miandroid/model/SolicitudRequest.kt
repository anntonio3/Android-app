package mx.edu.unpa.miandroid.model

data class SolicitudRequest(
    val nombreCompleto: String,
    val telefono: String,
    val direccion: String,
    val tipoVivienda: String,
    val tienePatio: Boolean,
    val otrasMascotas: Boolean,
    val experiencia: String,
    val motivo: String,
    val mensaje: String
)