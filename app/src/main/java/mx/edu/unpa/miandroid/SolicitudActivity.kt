package mx.edu.unpa.miandroid

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.SolicitudRequest
import mx.edu.unpa.miandroid.model.SolicitudResponse
import mx.edu.unpa.miandroid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitudActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var spinnerVivienda: Spinner
    private lateinit var switchPatio: Switch
    private lateinit var switchOtrasMascotas: Switch
    private lateinit var etExperiencia: EditText
    private lateinit var etMotivo: EditText
    private lateinit var btnEnviar: View
    private lateinit var progressBar: View

    private var idMascota: Int = -1
    private var nombreMascota: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud)

        idMascota     = intent.getIntExtra("idMascota", -1)
        nombreMascota = intent.getStringExtra("nombreMascota") ?: "esta mascota"

        supportActionBar?.apply {
            title = "Solicitud de adopción"
            setDisplayHomeAsUpEnabled(true)
        }

        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        etTelefono       = findViewById(R.id.etTelefono)
        etDireccion      = findViewById(R.id.etDireccion)
        spinnerVivienda  = findViewById(R.id.spinnerVivienda)
        switchPatio      = findViewById(R.id.switchPatio)
        switchOtrasMascotas = findViewById(R.id.switchOtrasMascotas)
        etExperiencia    = findViewById(R.id.etExperiencia)
        etMotivo         = findViewById(R.id.etMotivo)
        btnEnviar        = findViewById(R.id.btnEnviar)
        progressBar      = findViewById(R.id.progressBar)

        findViewById<TextView>(R.id.tvTituloMascota).text =
            "Quieres adoptar a $nombreMascota"

        spinnerVivienda.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Casa", "Departamento", "Otro"))

        btnEnviar.setOnClickListener { enviarSolicitud() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun enviarSolicitud() {
        val nombre    = etNombreCompleto.text.toString().trim()
        val telefono  = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val motivo    = etMotivo.text.toString().trim()

        if (nombre.isEmpty())    { etNombreCompleto.error = "Requerido"; return }
        if (telefono.isEmpty())  { etTelefono.error = "Requerido"; return }
        if (direccion.isEmpty()) { etDireccion.error = "Requerido"; return }
        if (motivo.isEmpty())    { etMotivo.error = "Cuéntanos por qué"; return }

        val idUsuario = SessionManager.getIdUsuario(this)
        if (idUsuario == -1 || idMascota == -1) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnEnviar.isEnabled = false

        val request = SolicitudRequest(
            nombreCompleto = nombre,
            telefono       = telefono,
            direccion      = direccion,
            tipoVivienda   = spinnerVivienda.selectedItem as String,
            tienePatio     = switchPatio.isChecked,
            otrasMascotas  = switchOtrasMascotas.isChecked,
            experiencia    = etExperiencia.text.toString().trim(),
            motivo         = motivo,
            mensaje        = "Solicitud de adopción para $nombreMascota"
        )

        RetrofitClient.adoptame.crearSolicitud(idMascota, idUsuario, request)
            .enqueue(object : Callback<SolicitudResponse> {
                override fun onResponse(
                    call: Call<SolicitudResponse>,
                    response: Response<SolicitudResponse>
                ) {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    if (response.isSuccessful) {
                        Toast.makeText(this@SolicitudActivity,
                            "¡Solicitud enviada! El dueño la revisará pronto.",
                            Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@SolicitudActivity,
                            "Error al enviar (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<SolicitudResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnEnviar.isEnabled = true
                    Toast.makeText(this@SolicitudActivity,
                        "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}