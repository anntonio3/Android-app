package mx.edu.unpa.miandroid.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.DetalleMascotaActivity
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.MascotaList

class MascotasAdapter(
    private var lista: List<MascotaList>,
    private val nombreTipo: String
) : RecyclerView.Adapter<MascotasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMascota: ImageView  = view.findViewById(R.id.imgMascota)
        val tvNombre: TextView     = view.findViewById(R.id.tvNombreMascota)
        val tvTipo: TextView       = view.findViewById(R.id.tvTipoMascota)
        val tvRaza: TextView       = view.findViewById(R.id.tvRaza)
        val tvSexo: TextView       = view.findViewById(R.id.tvSexo)
        val tvEstado: TextView     = view.findViewById(R.id.tvEstado)
        val cardEstado: CardView   = view.findViewById(R.id.cardEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m   = lista[position]
        val ctx = holder.itemView.context

        holder.tvNombre.text = m.nombre
        holder.tvTipo.text   = m.tipoMascota
        holder.tvRaza.text   = m.raza ?: "Mestizo"
        holder.tvSexo.text   = m.sexo
        holder.tvEstado.text = m.estadoAdopcion

        // Color badge estado (igual que antes)
        val (bgColor, txtColor) = when (
            m.estadoAdopcion.lowercase().replace(" ", "_")) {
            "disponible" -> Pair(R.color.estadoDisponibleBg, R.color.estadoDisponibleTxt)
            "en_proceso" -> Pair(R.color.estadoEnProcesoBg,  R.color.estadoEnProcesoTxt)
            "adoptado"   -> Pair(R.color.estadoAdoptadoBg,   R.color.estadoAdoptadoTxt)
            else         -> Pair(R.color.estadoDisponibleBg, R.color.estadoDisponibleTxt)
        }
        holder.cardEstado.setCardBackgroundColor(ctx.getColor(bgColor))
        holder.tvEstado.setTextColor(ctx.getColor(txtColor))

        // ← NUEVO: foto real si existe, genérica si no
        val urlFoto = m.urlFotoPrincipal   // nuevo campo en MascotaList
        if (!urlFoto.isNullOrEmpty()) {
            Glide.with(ctx)
                .load(urlFoto)
                .centerCrop()
                .placeholder(imagenGenericaPorTipo(m.tipoMascota))
                .error(imagenGenericaPorTipo(m.tipoMascota))
                .into(holder.imgMascota)
        } else {
            holder.imgMascota.setImageResource(imagenGenericaPorTipo(m.tipoMascota))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(ctx, DetalleMascotaActivity::class.java).apply {
                putExtra("mascota", m)
            }
            ctx.startActivity(intent)
        }
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

    fun actualizar(nuevaLista: List<MascotaList>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}