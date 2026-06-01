package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.LoginRequest
import mx.edu.unpa.miandroid.model.LoginResponse
import mx.edu.unpa.miandroid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: View
    private lateinit var tvOlvide: TextView
    private lateinit var tvRegistrate: TextView
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay sesión activa, ir directo a Categorías
        if (SessionManager.isLoggedIn(this)) {
            irACategorias()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail     = findViewById(R.id.etEmail)
        etPassword  = findViewById(R.id.etPassword)
        btnLogin    = findViewById(R.id.btnLogin)
        tvOlvide    = findViewById(R.id.tvOlvideContrasena)
        tvRegistrate = findViewById(R.id.tvRegistrate)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener { intentarLogin() }

        tvOlvide.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenaActivity::class.java))
        }

        tvRegistrate.setOnClickListener {
            startActivity(Intent(this, CrearCuentaActivity::class.java))
        }
    }

    private fun intentarLogin() {
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validaciones
        if (email.isEmpty()) { etEmail.error = "Ingresa tu correo"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo no válido"; return
        }

        // Por esta:
        //if (!email.endsWith("@gmail.com")) {
        //    etEmail.error = "Solo se permiten correos @gmail.com"; return
        //}

        if (password.isEmpty()) { etPassword.error = "Ingresa tu contraseña"; return }

        setLoading(true)

        // Reemplaza el enqueue completo por esto:
        RetrofitClient.adoptame.login(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        SessionManager.guardar(
                            this@LoginActivity,
                            user.idUsuario,
                            user.nombre,
                            user.email
                        )
                        irACategorias()
                    } else {
                        // Leer el error sin crashear
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error desconocido"
                        } catch (e: Exception) { "Error al procesar respuesta" }

                        android.util.Log.e("LOGIN", "Error ${response.code()}: $errorMsg")
                        Toast.makeText(
                            this@LoginActivity,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    android.util.Log.e("LOGIN", "Fallo red: ${t.message}", t)
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun irACategorias() {
        startActivity(Intent(this, CategoriasActivity::class.java))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
    }
}