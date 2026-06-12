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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.UploadFile
import mx.edu.unpa.miandroid.model.Usuario
import mx.edu.unpa.miandroid.model.UsuarioUpdate
import mx.edu.unpa.miandroid.util.SessionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PerfilActivity : AppCompatActivity() {

    private lateinit var imgPerfil: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etApellidoP: EditText
    private lateinit var etApellidoM: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnGuardar: View
    private lateinit var btnCambiarFoto: View
    private lateinit var btnMisMascotas: View
    private lateinit var btnMisSolicitudes: View
    private lateinit var progressBar: View

    private var fotoUri: Uri? = null
    private var fotoBitmap: Bitmap? = null
    private var fotoUrlActual: String? = null   // foto ya guardada en el server

    // ── Lanzadores ────────────────────────────────────────────────────
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            fotoUri = it
            fotoBitmap = null
            Glide.with(this).load(it).circleCrop().into(imgPerfil)
        }
    }

    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            fotoBitmap = it
            fotoUri = null
            imgPerfil.setImageBitmap(it)
        }
    }

    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) camaraLauncher.launch(null)
        else Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        supportActionBar?.apply {
            title = "Mi perfil"
            setDisplayHomeAsUpEnabled(true)
        }

        imgPerfil   = findViewById(R.id.imgPerfil)
        etNombre    = findViewById(R.id.etNombre)
        etApellidoP = findViewById(R.id.etApellidoPaterno)
        etApellidoM = findViewById(R.id.etApellidoMaterno)
        etTelefono  = findViewById(R.id.etTelefono)
        btnGuardar  = findViewById(R.id.btnGuardar)
        btnCambiarFoto    = findViewById(R.id.btnCambiarFoto)
        btnMisMascotas    = findViewById(R.id.btnMisMascotas)
        btnMisSolicitudes = findViewById(R.id.btnMisSolicitudes)
        progressBar = findViewById(R.id.progressBar)

        cargarPerfil()

        btnCambiarFoto.setOnClickListener { mostrarDialogoFoto() }
        btnGuardar.setOnClickListener { guardarPerfil() }
        btnMisMascotas.setOnClickListener {
            startActivity(Intent(this, MisMascotasActivity::class.java))
        }
        btnMisSolicitudes.setOnClickListener {
            startActivity(Intent(this, MisSolicitudesActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun mostrarDialogoFoto() {
        AlertDialog.Builder(this)
            .setTitle("Foto de perfil")
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

    private fun cargarPerfil() {
        val id = SessionManager.getIdUsuario(this)
        progressBar.visibility = View.VISIBLE

        RetrofitClient.adoptame.getUsuario(id)
            .enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val u = response.body() ?: return
                        etNombre.setText(u.nombre)
                        etApellidoP.setText(u.apellidoPaterno)
                        etApellidoM.setText(u.apellidoMaterno ?: "")
                        etTelefono.setText(u.telefono ?: "")
                        fotoUrlActual = u.foto
                        if (!u.foto.isNullOrEmpty()) {
                            Glide.with(this@PerfilActivity).load(u.foto)
                                .circleCrop()
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(imgPerfil)
                        }
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@PerfilActivity,
                        "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun guardarPerfil() {
        progressBar.visibility = View.VISIBLE

        // Si hay foto nueva, subirla primero
        if (fotoUri != null || fotoBitmap != null) {
            subirFotoYGuardar()
        } else {
            // Sin foto nueva — guardar con la URL actual
            actualizarPerfil(fotoUrlActual)
        }
    }

    private fun subirFotoYGuardar() {
        val archivo = crearArchivoTemporal() ?: run {
            actualizarPerfil(fotoUrlActual)
            return
        }

        val requestFile = archivo.asRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData("file", archivo.name, requestFile)

        RetrofitClient.adoptame.subirImagen(part)
            .enqueue(object : Callback<UploadFile> {
                override fun onResponse(call: Call<UploadFile>, response: Response<UploadFile>) {
                    val url = response.body()?.ruta
                    Log.d("PERFIL_FOTO", "Foto subida: $url")
                    actualizarPerfil(url ?: fotoUrlActual)
                }
                override fun onFailure(call: Call<UploadFile>, t: Throwable) {
                    Log.e("PERFIL_FOTO", "Error subiendo: ${t.message}")
                    actualizarPerfil(fotoUrlActual)
                }
            })
    }

    private fun actualizarPerfil(urlFoto: String?) {
        val id = SessionManager.getIdUsuario(this)

        val datos = UsuarioUpdate(
            nombre          = etNombre.text.toString().trim(),
            apellidoPaterno = etApellidoP.text.toString().trim(),
            apellidoMaterno = etApellidoM.text.toString().trim(),
            telefono        = etTelefono.text.toString().trim(),
            foto            = urlFoto
        )

        RetrofitClient.adoptame.actualizarPerfil(id, datos)
            .enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        fotoUrlActual = urlFoto
                        Toast.makeText(this@PerfilActivity,
                            "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PerfilActivity,
                            "Error al guardar (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@PerfilActivity,
                        "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun crearArchivoTemporal(): File? {
        return try {
            val archivo = File(cacheDir, "perfil_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(archivo)
            when {
                fotoBitmap != null ->
                    fotoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                fotoUri != null -> {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fotoUri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                }
                else -> return null
            }
            fos.flush(); fos.close()
            archivo
        } catch (e: Exception) {
            Log.e("PERFIL_FOTO", "Error archivo: ${e.message}")
            null
        }
    }
}