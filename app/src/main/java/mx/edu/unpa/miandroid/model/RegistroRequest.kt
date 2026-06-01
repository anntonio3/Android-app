package mx.edu.unpa.miandroid.model

data class RegistroRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val email: String,
    val password: String,
    val telefono: String? = null
)