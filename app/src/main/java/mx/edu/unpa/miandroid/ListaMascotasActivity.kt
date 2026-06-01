package mx.edu.unpa.miandroid

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.MascotasAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.MascotaList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaMascotasActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvVacia: TextView
    private lateinit var adapter: MascotasAdapter
    private var idTipoMascota: Int = 1
    private var nombreTipo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_mascotas)

        idTipoMascota = intent.getIntExtra("idTipoMascota", 1)
        nombreTipo    = intent.getStringExtra("nombreTipo") ?: "Mascotas"

        // Usa la ActionBar del tema directamente
        supportActionBar?.apply {
            title = nombreTipo
            setDisplayHomeAsUpEnabled(true)
        }

        recycler    = findViewById(R.id.recyclerMascotas)
        progressBar = findViewById(R.id.progressBar)
        tvVacia     = findViewById(R.id.tvListaVacia)

        adapter = MascotasAdapter(emptyList(), nombreTipo)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        cargarMascotas()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun cargarMascotas() {
        progressBar.visibility = View.VISIBLE
        tvVacia.visibility = View.GONE

        RetrofitClient.adoptame.getMascotasPorTipo(idTipoMascota)
            .enqueue(object : Callback<List<MascotaList>> {
                override fun onResponse(
                    call: Call<List<MascotaList>>,
                    response: Response<List<MascotaList>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val lista = response.body() ?: emptyList()
                        adapter.actualizar(lista)
                        tvVacia.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Toast.makeText(this@ListaMascotasActivity,
                            "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<MascotaList>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ListaMascotasActivity,
                        "Sin conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}