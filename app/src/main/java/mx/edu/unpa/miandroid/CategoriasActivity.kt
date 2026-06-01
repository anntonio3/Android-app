package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.CategoriasAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.TipoMascotaResumen
import mx.edu.unpa.miandroid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriasActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: View
    private lateinit var adapter: CategoriasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        supportActionBar?.title = "AdoptaMe"

        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)

        recycler    = findViewById(R.id.recyclerCategorias)
        progressBar = findViewById(R.id.progressBar)

        adapter = CategoriasAdapter(emptyList()) { tipo ->
            val intent = Intent(this, ListaMascotasActivity::class.java).apply {
                putExtra("idTipoMascota", tipo.idTipoMascota)
                putExtra("nombreTipo", tipo.descripcion)
            }
            startActivity(intent)
        }

        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter

        cargarCategorias()
    }

    override fun onResume() {
        super.onResume()
        cargarCategorias()   // refresca el conteo al volver de registrar mascota
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_categorias, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_agregar -> {
                startActivity(Intent(this, RegistrarMascotaActivity::class.java))
                true
            }
            R.id.action_logout -> {
                SessionManager.cerrarSesion(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cargarCategorias() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.adoptame.getTiposResumen()
            .enqueue(object : Callback<List<TipoMascotaResumen>> {
                override fun onResponse(
                    call: Call<List<TipoMascotaResumen>>,
                    response: Response<List<TipoMascotaResumen>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        adapter.actualizar(response.body() ?: emptyList())
                    } else {
                        Toast.makeText(this@CategoriasActivity,
                            "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<TipoMascotaResumen>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CategoriasActivity,
                        "Sin conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}