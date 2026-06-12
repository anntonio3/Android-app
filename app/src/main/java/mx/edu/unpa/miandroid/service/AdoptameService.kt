package mx.edu.unpa.miandroid.service

import mx.edu.unpa.miandroid.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface AdoptameService {

    // ── Auth ──────────────────────────────────────────────────────────────
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/registro")
    fun registro(@Body request: RegistroRequest): Call<LoginResponse>

    // ── Tipos de mascota ──────────────────────────────────────────────────
    @GET("api/tipos-mascota/resumen")
    fun getTiposResumen(): Call<List<TipoMascotaResumen>>

    @GET("api/tipos-mascota")
    fun getTipos(): Call<List<CatTipoMascota>>

    // ── Mascotas ──────────────────────────────────────────────────────────
    @GET("api/mascotas/tipo/{idTipo}")
    fun getMascotasPorTipo(@Path("idTipo") idTipo: Int): Call<List<MascotaList>>

    // ← Ahora devuelve MascotaResponseDTO para obtener el idMascota
    @POST("api/mascotas/agregar")
    fun registrarMascota(
        @Query("idDonador") idDonador: Int,
        @Body mascota: MascotaRequest
    ): Call<MascotaResponseDTO>

    // ── Upload ────────────────────────────────────────────────────────
    @Multipart
    @POST("api/upload")
    fun subirImagen(
        @Part file: MultipartBody.Part
    ): Call<UploadFile>

    // ── Imagen Mascota ────────────────────────────────────────────────
    @POST("api/imagenes")
    fun guardarImagenMascota(
        @Query("idMascota") idMascota: Int,
        @Body imagen: ImagenMascotaRequest
    ): Call<Any>

    @GET("api/imagenes/mascota/{idMascota}")
    fun getImagenesMascota(@Path("idMascota") idMascota: Int): Call<List<ImagenMascota>>


    // ── Perfil ──
    @PATCH("api/usuarios/{id}/perfil")
    fun actualizarPerfil(
        @Path("id") id: Int,
        @Body datos: UsuarioUpdate
    ): Call<Any>

    @GET("api/usuarios/{id}")
    fun getUsuario(@Path("id") id: Int): Call<Usuario>

    // ── Mis mascotas ──
    @GET("api/mascotas/usuario/{idUsuario}")
    fun getMascotasUsuario(@Path("idUsuario") idUsuario: Int): Call<List<MascotaResponseDTO>>

    // ── Solicitudes ──
    @POST("api/solicitudes")
    fun crearSolicitud(
        @Query("idMascota") idMascota: Int,
        @Query("idUsuario") idUsuario: Int,
        @Body solicitud: SolicitudRequest
    ): Call<SolicitudResponse>

    @GET("api/solicitudes/solicitante/{idUsuario}")
    fun getMisSolicitudes(@Path("idUsuario") idUsuario: Int): Call<List<SolicitudResponse>>

    @GET("api/solicitudes/recibidas/{idDonador}")
    fun getSolicitudesRecibidas(@Path("idDonador") idDonador: Int): Call<List<SolicitudResponse>>

    @PATCH("api/solicitudes/{id}/estado")
    fun cambiarEstadoSolicitud(
        @Path("id") id: Int,
        @Query("estado") estado: String
    ): Call<SolicitudResponse>
}