package mx.edu.unpa.miandroid.client

import mx.edu.unpa.miandroid.service.UsuarioService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.110.233:8181/"

    val instance: UsuarioService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioService::class.java)
    }

}