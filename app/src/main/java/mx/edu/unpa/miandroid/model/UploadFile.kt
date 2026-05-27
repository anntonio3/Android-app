package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class UploadFile(
    val nombre: String,
    val ruta: String,
    val tipo: String,
    val size: Long


) : Serializable
