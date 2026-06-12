package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.SolicitudResponse

class SolicitudesRecibidasAdapter(
    private var lista: List<SolicitudResponse>,
    private val onAceptar: (SolicitudResponse) -> Unit,
    private val onRechazar: (SolicitudResponse) -> Unit
) : RecyclerView.Adapter<SolicitudesRecibidasAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgMascota: ImageView = v.findViewById(R.id.imgMascota)
        val tvMascota: TextView   = v.findViewById(R.id.tvMascota)
        val tvSolicitante: TextView = v.findViewById(R.id.tvSolicitante)
        val tvDatos: TextView     = v.findViewById(R.id.tvDatos)
        val tvEstado: TextView    = v.findViewById(R.id.tvEstado)
        val btnAceptar: Button    = v.findViewById(R.id.btnAceptar)
        val btnRechazar: Button   = v.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_solicitud_recibida, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = lista[position]
        val ctx = holder.itemView.context

        holder.tvMascota.text     = "🐾 ${s.nombreMascota ?: ""}"
        holder.tvSolicitante.text = "Solicitante: ${s.nombreCompleto ?: s.nombreSolicitante ?: ""}"
        holder.tvDatos.text = buildString {
            append("📞 ${s.telefono ?: "N/A"}\n")
            append("🏠 ${s.tipoVivienda ?: "N/A"}")
            if (s.tienePatio == true) append(" con patio")
            append("\n💬 ${s.motivo ?: ""}")
        }
        holder.tvEstado.text = s.estadoSolicitud

        // Imagen
        val imgRes = imagenGenericaPorTipo(s.tipoMascota ?: "")
        if (!s.urlFotoMascota.isNullOrEmpty()) {
            Glide.with(ctx).load(s.urlFotoMascota).centerCrop()
                .placeholder(imgRes).error(imgRes).into(holder.imgMascota)
        } else {
            holder.imgMascota.setImageResource(imgRes)
        }

        // Botones solo si está Pendiente
        val pendiente = s.estadoSolicitud.equals("Pendiente", ignoreCase = true)
        holder.btnAceptar.visibility  = if (pendiente) View.VISIBLE else View.GONE
        holder.btnRechazar.visibility = if (pendiente) View.VISIBLE else View.GONE

        holder.btnAceptar.setOnClickListener  { onAceptar(s) }
        holder.btnRechazar.setOnClickListener { onRechazar(s) }
    }

    fun actualizar(nueva: List<SolicitudResponse>) {
        lista = nueva
        notifyDataSetChanged()
    }

    private fun imagenGenericaPorTipo(tipo: String) = when (tipo.trim().lowercase()) {
        "perro"   -> R.drawable.img_perro
        "gato"    -> R.drawable.img_gato
        "loro"    -> R.drawable.img_loro
        "hamster" -> R.drawable.img_hamster
        else      -> android.R.drawable.ic_menu_gallery
    }
}