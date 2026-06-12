package mx.edu.unpa.miandroid

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.SolicitudesRecibidasAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.SolicitudResponse
import mx.edu.unpa.miandroid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisMascotasActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var progressBar: View
    private lateinit var adapter: SolicitudesRecibidasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_mascotas)

        supportActionBar?.apply {
            title = "Solicitudes a mis mascotas"
            setDisplayHomeAsUpEnabled(true)
        }

        recycler    = findViewById(R.id.recyclerSolicitudes)
        tvVacio     = findViewById(R.id.tvVacio)
        progressBar = findViewById(R.id.progressBar)

        adapter = SolicitudesRecibidasAdapter(emptyList(),
            onAceptar = { sol -> cambiarEstado(sol, "Aprobada") },
            onRechazar = { sol -> cambiarEstado(sol, "Rechazada") }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        cargarSolicitudes()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun cargarSolicitudes() {
        val idDonador = SessionManager.getIdUsuario(this)
        progressBar.visibility = View.VISIBLE
        tvVacio.visibility = View.GONE

        RetrofitClient.adoptame.getSolicitudesRecibidas(idDonador)
            .enqueue(object : Callback<List<SolicitudResponse>> {
                override fun onResponse(
                    call: Call<List<SolicitudResponse>>,
                    response: Response<List<SolicitudResponse>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val lista = response.body() ?: emptyList()
                        adapter.actualizar(lista)
                        tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                override fun onFailure(call: Call<List<SolicitudResponse>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MisMascotasActivity,
                        "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cambiarEstado(sol: SolicitudResponse, estado: String) {
        RetrofitClient.adoptame.cambiarEstadoSolicitud(sol.idSolicitud, estado)
            .enqueue(object : Callback<SolicitudResponse> {
                override fun onResponse(
                    call: Call<SolicitudResponse>,
                    response: Response<SolicitudResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MisMascotasActivity,
                            "Solicitud $estado", Toast.LENGTH_SHORT).show()
                        cargarSolicitudes()
                    }
                }
                override fun onFailure(call: Call<SolicitudResponse>, t: Throwable) {
                    Toast.makeText(this@MisMascotasActivity,
                        "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}