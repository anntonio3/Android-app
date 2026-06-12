package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.adapter.FotosMascotaAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.ImagenMascota
import mx.edu.unpa.miandroid.model.MascotaList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleMascotaActivity : AppCompatActivity() {

    private lateinit var imgPrincipal: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvRaza: TextView
    private lateinit var tvSexo: TextView
    private lateinit var tvEstado: TextView
    private lateinit var recyclerFotos: RecyclerView
    private lateinit var tvSinFotos: TextView
    private lateinit var progressBar: View

    private lateinit var adapter: FotosMascotaAdapter
    private var mascota: MascotaList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_mascota)

        mascota = intent.getSerializableExtra("mascota") as? MascotaList

        supportActionBar?.apply {
            title = mascota?.nombre ?: "Detalle"
            setDisplayHomeAsUpEnabled(true)
        }

        imgPrincipal  = findViewById(R.id.imgPrincipal)
        tvNombre      = findViewById(R.id.tvNombre)
        tvTipo        = findViewById(R.id.tvTipo)
        tvRaza        = findViewById(R.id.tvRaza)
        tvSexo        = findViewById(R.id.tvSexo)
        tvEstado      = findViewById(R.id.tvEstado)
        recyclerFotos = findViewById(R.id.recyclerFotos)
        tvSinFotos    = findViewById(R.id.tvSinFotos)
        progressBar   = findViewById(R.id.progressBar)

        adapter = FotosMascotaAdapter(emptyList())
        recyclerFotos.layoutManager = GridLayoutManager(this, 2)
        recyclerFotos.adapter = adapter

        llenarDatos()
        cargarFotos()

        // Botón adoptar
        val btnAdoptar = findViewById<Button>(R.id.btnAdoptar)
        btnAdoptar.setOnClickListener {
            val m = mascota ?: return@setOnClickListener
            // No permitir solicitar si ya está adoptada
            if (m.estadoAdopcion.equals("Adoptado", ignoreCase = true)) {
                Toast.makeText(this, "Esta mascota ya fue adoptada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SolicitudActivity::class.java).apply {
                putExtra("idMascota", m.idMascota)
                putExtra("nombreMascota", m.nombre)
            }
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun llenarDatos() {
        val m = mascota ?: return

        tvNombre.text = m.nombre
        tvTipo.text   = m.tipoMascota
        tvRaza.text   = m.raza ?: "Mestizo"
        tvSexo.text   = m.sexo
        tvEstado.text = m.estadoAdopcion

        // Color del estado
        val (bg, txt) = when (m.estadoAdopcion.lowercase().replace(" ", "_")) {
            "disponible" -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
            "en_proceso" -> Pair(0xFFFFF8E1.toInt(), 0xFFF57F17.toInt())
            "adoptado"   -> Pair(0xFFF5F5F5.toInt(), 0xFF757575.toInt())
            else         -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
        }
        tvEstado.setBackgroundColor(bg)
        tvEstado.setTextColor(txt)

        // Imagen principal
        val imgRes = imagenGenericaPorTipo(m.tipoMascota)
        if (!m.urlFotoPrincipal.isNullOrEmpty()) {
            Glide.with(this)
                .load(m.urlFotoPrincipal)
                .centerCrop()
                .placeholder(imgRes)
                .error(imgRes)
                .into(imgPrincipal)
        } else {
            imgPrincipal.setImageResource(imgRes)
        }
    }

    private fun cargarFotos() {
        val idMascota = mascota?.idMascota ?: return
        progressBar.visibility = View.VISIBLE

        RetrofitClient.adoptame.getImagenesMascota(idMascota)
            .enqueue(object : Callback<List<ImagenMascota>> {
                override fun onResponse(
                    call: Call<List<ImagenMascota>>,
                    response: Response<List<ImagenMascota>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val fotos = response.body() ?: emptyList()
                        if (fotos.isEmpty()) {
                            // Sin fotos reales — mostrar la genérica varias veces
                            tvSinFotos.visibility = View.GONE
                            recyclerFotos.visibility = View.VISIBLE
                            adapter.actualizarConGenerica(
                                mascota?.urlFotoPrincipal,
                                imagenGenericaPorTipo(mascota?.tipoMascota ?: "")
                            )
                        } else {
                            tvSinFotos.visibility = View.GONE
                            recyclerFotos.visibility = View.VISIBLE
                            adapter.actualizar(fotos)
                        }
                    }
                }

                override fun onFailure(call: Call<List<ImagenMascota>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@DetalleMascotaActivity,
                        "Error al cargar fotos", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun imagenGenericaPorTipo(tipo: String): Int {
        return when (tipo.trim().lowercase()) {
            "perro"   -> R.drawable.img_perro
            "gato"    -> R.drawable.img_gato
            "loro"    -> R.drawable.img_loro
            "hamster" -> R.drawable.img_hamster
            else      -> android.R.drawable.ic_menu_gallery
        }
    }
}