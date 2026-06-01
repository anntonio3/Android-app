package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.TipoMascotaResumen

class CategoriasAdapter(
    private var lista: List<TipoMascotaResumen>,
    private val onClick: (TipoMascotaResumen) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAnimal: ImageView = view.findViewById(R.id.imgAnimal)
        val tvTipo: TextView     = view.findViewById(R.id.tvTipoNombre)
        val tvDisp: TextView     = view.findViewById(R.id.tvDisponibles)
        val tvBadge: TextView    = view.findViewById(R.id.tvBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categorias, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tipo = lista[position]

        holder.tvTipo.text = tipo.descripcion
        holder.tvBadge.text = tipo.descripcion

        if (tipo.disponibles > 0) {
            holder.tvDisp.text = "${tipo.disponibles} disponibles"
            holder.tvDisp.setTextColor(
                holder.itemView.context.getColor(R.color.colorDisponible))
        } else {
            holder.tvDisp.text = "Sin disponibles"
            holder.tvDisp.setTextColor(
                holder.itemView.context.getColor(R.color.colorAgotado))
        }

        // Imagen local según tipo
        val imgRes = when (tipo.descripcion.lowercase()) {
            "perro"   -> R.drawable.img_perro
            "gato"    -> R.drawable.img_gato
            "loro"    -> R.drawable.img_loro
            "hamster" -> R.drawable.img_hamster
            else      -> R.drawable.img_perro
        }
        holder.imgAnimal.setImageResource(imgRes)

        holder.itemView.setOnClickListener { onClick(tipo) }
    }

    fun actualizar(nuevaLista: List<TipoMascotaResumen>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}