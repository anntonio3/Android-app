package mx.edu.unpa.miandroid.service

import android.text.Editable
import mx.edu.unpa.miandroid.model.UploadFile
import mx.edu.unpa.miandroid.model.Usuario
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {
    @GET("usuario/app")
    fun getUsuario(): Call<List<Usuario>>

    @GET("usuario/app/{id}")
    fun getUsuarioById(@Path("id") id: Int): Call<Usuario>

    @GET("usuario/app/buscar")
    fun getUsuarioByEmail(@Query("email") email: String): Call<Usuario>

    @POST("usuario/app/create")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("usuario/app/{id}")
    fun actualizarUsuario(@Path("id") id: Int, @Body usuario: Usuario): Call<Usuario>

    @DELETE("usuario/app/{id}")
    fun eliminarUsuario(@Path("id") id: Int): Call<Void>

    // Upload image
    @Multipart
    @POST("api/upload")
    fun uploadImage(
         @Part file: MultipartBody.Part
    ): Call<UploadFile>

}