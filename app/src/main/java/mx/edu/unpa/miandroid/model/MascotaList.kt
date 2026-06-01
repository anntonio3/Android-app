package mx.edu.unpa.miandroid.model

data class MascotaList(
    val idMascota: Int,
    val nombre: String,
    val raza: String?,
    val sexo: String,
    val estadoAdopcion: String,
    val tipoMascota: String ,       // descripción: "Perro", "Gato", etc.
    val urlFotoPrincipal: String? = null   // ← nuevo campo
)