package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.SolicitudResponse

class MisSolicitudesAdapter(
    private var lista: List<SolicitudResponse>
) : RecyclerView.Adapter<MisSolicitudesAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgMascota: ImageView = v.findViewById(R.id.imgMascota)
        val tvMascota: TextView   = v.findViewById(R.id.tvMascota)
        val tvFecha: TextView     = v.findViewById(R.id.tvFecha)
        val tvEstado: TextView    = v.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mi_solicitud, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = lista[position]
        val ctx = holder.itemView.context

        holder.tvMascota.text = "${s.nombreMascota ?: ""} (${s.tipoMascota ?: ""})"
        holder.tvFecha.text   = "Enviada: ${s.fechaSolicitud?.take(10) ?: ""}"
        holder.tvEstado.text  = s.estadoSolicitud

        // Color según estado
        val (bg, txt) = when (s.estadoSolicitud.lowercase()) {
            "aprobada"  -> Pair(0xFFE8F5E9.toInt(), 0xFF2E7D32.toInt())
            "rechazada" -> Pair(0xFFFFEBEE.toInt(), 0xFFC62828.toInt())
            else        -> Pair(0xFFFFF8E1.toInt(), 0xFFF57F17.toInt()) // Pendiente
        }
        holder.tvEstado.setBackgroundColor(bg)
        holder.tvEstado.setTextColor(txt)

        val imgRes = imagenGenericaPorTipo(s.tipoMascota ?: "")
        if (!s.urlFotoMascota.isNullOrEmpty()) {
            Glide.with(ctx).load(s.urlFotoMascota).centerCrop()
                .placeholder(imgRes).error(imgRes).into(holder.imgMascota)
        } else {
            holder.imgMascota.setImageResource(imgRes)
        }
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