package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class SolicitudResponse(
    val idSolicitud: Int,
    val idMascota: Int,
    val nombreMascota: String?,
    val tipoMascota: String?,
    val urlFotoMascota: String?,
    val idSolicitante: Int,
    val nombreSolicitante: String?,
    val nombreCompleto: String?,
    val telefono: String?,
    val direccion: String?,
    val tipoVivienda: String?,
    val tienePatio: Boolean?,
    val otrasMascotas: Boolean?,
    val experiencia: String?,
    val motivo: String?,
    val mensaje: String?,
    val estadoSolicitud: String,
    val fechaSolicitud: String?
) : Serializable