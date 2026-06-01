package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnEnviar: View
    private lateinit var tvVolver: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        etEmail  = findViewById(R.id.etEmail)
        btnEnviar = findViewById(R.id.btnEnviar)
        tvVolver  = findViewById(R.id.tvVolverLogin)

        btnEnviar.setOnClickListener { enviarCorreo() }
        tvVolver.setOnClickListener  { finish() }
    }

    private fun enviarCorreo() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) { etEmail.error = "Ingresa tu correo"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo no válido"; return
        }
        // En una app real aquí se llamaría al endpoint de recuperación.
        // Por ahora mostramos confirmación y volvemos al login.
        Toast.makeText(this,
            "Si el correo está registrado recibirás instrucciones", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }
}