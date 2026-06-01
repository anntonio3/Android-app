// Ya existe en tu proyecto — solo verifica que tenga estos campos:
package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class UploadFile(
    val nombre: String,
    val ruta: String,      // ← URL completa de la imagen
    val tipo: String,
    val size: String       // ← era Long, cámbialo a String para compatibilidad
) : Serializable
