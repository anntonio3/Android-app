package mx.edu.unpa.miandroid.client

import mx.edu.unpa.miandroid.service.AdoptameService
import mx.edu.unpa.miandroid.service.UsuarioService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ⚠️ Cambia esta IP a la de tu máquina cuando sea necesario
    private const val BASE_URL = "http://192.168.1.68:8181/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio nuevo (AdoptaMe)
    val adoptame: AdoptameService by lazy {
        retrofit.create(AdoptameService::class.java)
    }

    // Servicio anterior (compatible con código existente)
    val instance: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }
}