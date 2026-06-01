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
}