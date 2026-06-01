package mx.edu.unpa.miandroid.model

data class ImagenMascotaRequest(
    val urlImagen: String,
    val imagenPrincipal: Boolean = true
)