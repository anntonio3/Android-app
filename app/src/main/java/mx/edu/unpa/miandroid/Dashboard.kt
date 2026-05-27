package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.UsuarioAdapter



class Dashboard : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    lateinit var recycler: RecyclerView
    lateinit var adapter: UsuarioAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = getSharedPreferences("sesion", MODE_PRIVATE)


        //val usuario = prefs.getString("usuario", null)

        // Get the Intent that started this activity
        //val intent = getIntent()

        //val usuario = intent.getStringExtra("usuario")
        //val contrasena = intent.getStringExtra("contrasena")
        //Toast.makeText(this, "Bienvenido: $usuario", Toast.LENGTH_LONG).show()

        recycler = findViewById(R.id.recyclerUsers)
        //adapter = UsuarioAdapter(emptyList())

        adapter = UsuarioAdapter(emptyList()){ usuario ->
            // Click
            //Toast.makeText(this, "Seleccionado: ${usuario.nombre}", Toast.LENGTH_SHORT).show()

            // Enviar a layout
            val intent = Intent(this, DetalleUsuario::class.java)
            //intent.putExtra("foto", usuario.foto)
            //intent.putExtra("nombre", usuario.nombre)
            //intent.putExtra("email", usuario.email)
            //intent.putExtra("telefono", usuario.telefono)

            intent.putExtra("usuario", usuario)

            Log.d("ENVIO", "enviando a detalle usuario")


            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        cargarUsuarios()
    }

    override fun onStart(){
        super.onStart()
        // Se cargan los recursos vizuales, el layout ya es visible. Pero el usuario no puede interactuar con la pantalla

    }

    override fun onResume(){
        super.onResume()
        // Ya se cargo el layout, el usuario puede interactuar los elemnentos en pantalla

        //val usuario = prefs.getString("usuario", null)
        //Toast.makeText(this, "Bienvenido: $usuario", Toast.LENGTH_LONG).show()

        cargarUsuarios()
    }

    override fun onPause(){
        super.onPause()
        // El layout pierde foco, pero se puede ver en pantalla
        // Entra una mensaje externo o notificacion push
        // Se abre otra actividad (Alarma)
    }

    override fun onStop(){
        super.onStop()
        // Se cambia a otra layout(pantalla)
        // Se minimiza el layout
    }

    override fun onRestart() {
        super.onRestart()
        // Se ejecuta cuando se regresa al layout (Pantalla(Actividad))
    }

    override fun onDestroy() {
        super.onDestroy()
        // El layout se destruye
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_config -> {
                Toast.makeText(this, "Configuracion", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_logout -> {
                // Toast.makeText(this, "Cerrar sesion", Toast.LENGTH_LONG)
                prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                prefs.edit().clear().apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun cargarUsuarios(){
        RetrofitClient.instance.getUsuario().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(
                call: Call<List<Usuario>?>,
                response: Response<List<Usuario>?>
            ) {
                if (response.isSuccessful) {
                    Log.d("RETROFIT", "Conexion exitosa!!")

                    var listaUsuarios = response.body() ?: emptyList()
                    adapter.actualizarLista(listaUsuarios)
                        Toast.makeText(this@Dashboard, "Total usuarios: ${listaUsuarios.size}", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<List<Usuario>?>, t: Throwable) {
                Toast.makeText(this@Dashboard, "Error ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }

        })
    }




}