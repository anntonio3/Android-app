package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.LoginResponse
import mx.edu.unpa.miandroid.model.RegistroRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearCuentaActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmar: EditText
    private lateinit var btnCrear: View
    private lateinit var tvLogin: View
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        etEmail    = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmar = findViewById(R.id.etConfirmarPassword)
        btnCrear   = findViewById(R.id.btnCrearCuenta)
        tvLogin    = findViewById(R.id.tvYaTengoCuenta)
        progressBar = findViewById(R.id.progressBar)

        btnCrear.setOnClickListener { intentarRegistro() }
        tvLogin.setOnClickListener { finish() }
    }

    private fun intentarRegistro() {
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmar = etConfirmar.text.toString().trim()

        if (email.isEmpty()) { etEmail.error = "Ingresa tu correo"; return }
        //if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        //    etEmail.error = "Correo no válido (@gmail.com, etc.)"; return
        //}
        // Por esta:
        if (!email.endsWith("@gmail.com")) {
            etEmail.error = "Solo se permiten correos @gmail.com"; return
        }
        if (password.isEmpty()) { etPassword.error = "Ingresa una contraseña"; return }
        if (password.length < 4) { etPassword.error = "Mínimo 4 caracteres"; return }
        if (confirmar != password) {
            etConfirmar.error = "Las contraseñas no coinciden"; return
        }

        setLoading(true)

        // El API requiere nombre y apellidos; usamos defaults desde el email
        val nombreBase = email.substringBefore("@")
        val request = RegistroRequest(
            nombre          = nombreBase,
            apellidoPaterno = "",
            apellidoMaterno = "",
            email           = email,
            password        = password
        )

        RetrofitClient.adoptame.registro(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearCuentaActivity,
                            "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CrearCuentaActivity, LoginActivity::class.java))
                        finishAffinity()
                    } else {
                        val msg = if (response.code() == 404 || response.code() == 400)
                            "El correo ya está registrado" else "Error al crear cuenta"
                        Toast.makeText(this@CrearCuentaActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(this@CrearCuentaActivity,
                        "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnCrear.isEnabled = !loading
    }
}