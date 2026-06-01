package mx.edu.unpa.miandroid.service

import mx.edu.unpa.miandroid.model.*
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

    @POST("api/mascotas/agregar")
    fun registrarMascota(
        @Query("idDonador") idDonador: Int,
        @Body mascota: MascotaRequest
    ): Call<Any>
}