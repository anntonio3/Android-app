package mx.edu.unpa.miandroid

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CatTipoMascota
import mx.edu.unpa.miandroid.model.MascotaRequest
import mx.edu.unpa.miandroid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarMascotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var etRaza: EditText
    private lateinit var spinnerSexo: Spinner
    private lateinit var btnRegistrar: View
    private lateinit var progressBar: View

    private var listaTipos: List<CatTipoMascota> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_mascota)

        supportActionBar?.apply {
            title = "Registrar mascota"
            setDisplayHomeAsUpEnabled(true)
        }

        etNombre    = findViewById(R.id.etNombre)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        etRaza      = findViewById(R.id.etRaza)
        spinnerSexo = findViewById(R.id.spinnerSexo)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        progressBar  = findViewById(R.id.progressBar)

        val sexos = listOf("Macho", "Hembra")
        spinnerSexo.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, sexos)

        cargarTipos()
        btnRegistrar.setOnClickListener { intentarRegistro() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun cargarTipos() {
        RetrofitClient.adoptame.getTipos()
            .enqueue(object : Callback<List<CatTipoMascota>> {
                override fun onResponse(
                    call: Call<List<CatTipoMascota>>,
                    response: Response<List<CatTipoMascota>>
                ) {
                    if (response.isSuccessful) {
                        listaTipos = response.body() ?: emptyList()
                        val nombres = listaTipos.map { it.descripcion }
                        spinnerTipo.adapter = ArrayAdapter(this@RegistrarMascotaActivity,
                            android.R.layout.simple_spinner_dropdown_item, nombres)
                    }
                }
                override fun onFailure(call: Call<List<CatTipoMascota>>, t: Throwable) {
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Sin conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun intentarRegistro() {
        val nombre = etNombre.text.toString().trim()
        val raza   = etRaza.text.toString().trim()

        if (nombre.isEmpty()) { etNombre.error = "Ingresa el nombre"; return }
        if (listaTipos.isEmpty()) {
            Toast.makeText(this, "Cargando tipos, espera un momento", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoSeleccionado = listaTipos[spinnerTipo.selectedItemPosition]
        val sexo = spinnerSexo.selectedItem as String
        val idDonador = SessionManager.getIdUsuario(this)

        if (idDonador == -1) {
            Toast.makeText(this, "Sesión expirada, inicia sesión de nuevo",
                Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = MascotaRequest(
            nombre       = nombre,
            idTipoMascota = tipoSeleccionado.idTipoMascota,
            raza         = raza.ifEmpty { "Mestizo" },
            sexo         = sexo
        )

        RetrofitClient.adoptame.registrarMascota(idDonador, request)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegistrarMascotaActivity,
                            "¡${nombre} registrado exitosamente!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegistrarMascotaActivity,
                            "Error al registrar (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnRegistrar.isEnabled = !loading
    }
}