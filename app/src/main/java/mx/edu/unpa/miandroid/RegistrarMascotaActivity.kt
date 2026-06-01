package mx.edu.unpa.miandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.*
import mx.edu.unpa.miandroid.util.SessionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RegistrarMascotaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var etRaza: EditText
    private lateinit var spinnerSexo: Spinner
    private lateinit var btnRegistrar: View
    private lateinit var progressBar: View
    private lateinit var imgPreview: ImageView
    private lateinit var btnAgregarFoto: View
    private lateinit var tvFotoHint: TextView

    private var listaTipos: List<CatTipoMascota> = emptyList()
    private var fotoUri: Uri? = null
    private var fotoBitmap: Bitmap? = null
    private var urlImagenSubida: String? = null

    // ── Lanzadores de resultado ───────────────────────────────────────

    // Galería
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            fotoUri = it
            fotoBitmap = null
            urlImagenSubida = null
            Glide.with(this).load(it).centerCrop().into(imgPreview)
            imgPreview.visibility = View.VISIBLE
            tvFotoHint.text = "Foto seleccionada ✓"
        }
    }

    // Cámara
    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            fotoBitmap = it
            fotoUri = null
            urlImagenSubida = null
            imgPreview.setImageBitmap(it)
            imgPreview.visibility = View.VISIBLE
            tvFotoHint.text = "Foto tomada ✓"
        }
    }

    // Permiso cámara
    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) camaraLauncher.launch(null)
        else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_mascota)

        supportActionBar?.apply {
            title = "Registrar mascota"
            setDisplayHomeAsUpEnabled(true)
        }

        etNombre     = findViewById(R.id.etNombre)
        spinnerTipo  = findViewById(R.id.spinnerTipo)
        etRaza       = findViewById(R.id.etRaza)
        spinnerSexo  = findViewById(R.id.spinnerSexo)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        progressBar  = findViewById(R.id.progressBar)
        imgPreview   = findViewById(R.id.imgPreview)
        btnAgregarFoto = findViewById(R.id.btnAgregarFoto)
        tvFotoHint   = findViewById(R.id.tvFotoHint)

        val sexos = listOf("Macho", "Hembra")
        spinnerSexo.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, sexos)

        cargarTipos()

        btnAgregarFoto.setOnClickListener { mostrarDialogoFoto() }
        btnRegistrar.setOnClickListener  { intentarRegistro() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // ── Foto ──────────────────────────────────────────────────────────

    private fun mostrarDialogoFoto() {
        AlertDialog.Builder(this)
            .setTitle("Agregar foto")
            .setItems(arrayOf("📷  Tomar foto", "🖼️  Elegir de galería")) { _, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> galeriaLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            camaraLauncher.launch(null)
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ── Registro ──────────────────────────────────────────────────────

    private fun cargarTipos() {
        RetrofitClient.adoptame.getTipos()
            .enqueue(object : Callback<List<CatTipoMascota>> {
                override fun onResponse(
                    call: Call<List<CatTipoMascota>>,
                    response: Response<List<CatTipoMascota>>
                ) {
                    if (response.isSuccessful) {
                        listaTipos = response.body() ?: emptyList()
                        spinnerTipo.adapter = ArrayAdapter(this@RegistrarMascotaActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            listaTipos.map { it.descripcion })
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
            Toast.makeText(this, "Espera que carguen los tipos", Toast.LENGTH_SHORT).show()
            return
        }

        val idDonador = SessionManager.getIdUsuario(this)
        if (idDonador == -1) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // Si hay foto pendiente de subir, primero sube y luego registra
        if (fotoUri != null || fotoBitmap != null) {
            subirFotoYRegistrar(nombre, raza, idDonador)
        } else {
            // Sin foto — registrar directo
            registrarMascota(nombre, raza, idDonador, null)
        }
    }

    private fun subirFotoYRegistrar(nombre: String, raza: String, idDonador: Int) {
        val archivo = crearArchivoTemporal() ?: run {
            setLoading(false)
            Toast.makeText(this, "Error al procesar la foto", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = archivo.asRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData("file", archivo.name, requestFile)

        RetrofitClient.adoptame.subirImagen(part)
            .enqueue(object : Callback<UploadFile> {
                override fun onResponse(call: Call<UploadFile>, response: Response<UploadFile>) {
                    if (response.isSuccessful) {
                        val url = response.body()?.ruta
                        Log.d("UPLOAD", "Foto subida: $url")
                        registrarMascota(nombre, raza, idDonador, url)
                    } else {
                        Log.e("UPLOAD", "Error subiendo foto: ${response.code()}")
                        // Si falla la foto, registrar sin ella
                        registrarMascota(nombre, raza, idDonador, null)
                    }
                }
                override fun onFailure(call: Call<UploadFile>, t: Throwable) {
                    Log.e("UPLOAD", "Fallo subiendo foto: ${t.message}")
                    // Si falla la red para la foto, registrar sin ella
                    registrarMascota(nombre, raza, idDonador, null)
                }
            })
    }

    private fun registrarMascota(
        nombre: String,
        raza: String,
        idDonador: Int,
        urlFoto: String?
    ) {
        val tipoSeleccionado = listaTipos[spinnerTipo.selectedItemPosition]
        val sexo = spinnerSexo.selectedItem as String

        val request = MascotaRequest(
            nombre        = nombre,
            idTipoMascota = tipoSeleccionado.idTipoMascota,
            raza          = raza.ifEmpty { "Mestizo" },
            sexo          = sexo
        )

        RetrofitClient.adoptame.registrarMascota(idDonador, request)
            .enqueue(object : Callback<MascotaResponseDTO> {
                override fun onResponse(
                    call: Call<MascotaResponseDTO>,
                    response: Response<MascotaResponseDTO>
                ) {
                    if (response.isSuccessful) {
                        val mascota = response.body()
                        val idMascota = mascota?.idMascota

                        // Si hay URL de foto, guardarla en ImagenMascota
                        if (urlFoto != null && idMascota != null) {
                            guardarImagenMascota(idMascota, urlFoto, nombre)
                        } else {
                            setLoading(false)
                            mostrarExito(nombre)
                        }
                    } else {
                        setLoading(false)
                        Toast.makeText(this@RegistrarMascotaActivity,
                            "Error al registrar (${response.code()})",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<MascotaResponseDTO>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun guardarImagenMascota(idMascota: Int, url: String, nombre: String) {
        RetrofitClient.adoptame.guardarImagenMascota(
            idMascota,
            ImagenMascotaRequest(urlImagen = url, imagenPrincipal = true)
        ).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                setLoading(false)
                mostrarExito(nombre)
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                setLoading(false)
                // La mascota quedó registrada aunque falle guardar la imagen
                mostrarExito(nombre)
            }
        })
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun crearArchivoTemporal(): File? {
        return try {
            val archivo = File(cacheDir, "mascota_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(archivo)

            when {
                fotoBitmap != null -> {
                    fotoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                }
                fotoUri != null -> {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fotoUri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                }
                else -> return null
            }

            fos.flush()
            fos.close()
            archivo
        } catch (e: Exception) {
            Log.e("FOTO", "Error creando archivo: ${e.message}")
            null
        }
    }

    private fun mostrarExito(nombre: String) {
        Toast.makeText(this, "¡$nombre registrado exitosamente!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnRegistrar.isEnabled = !loading
        btnAgregarFoto.isEnabled = !loading
    }
}