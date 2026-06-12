package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.ImagenMascota

class FotosMascotaAdapter(
    private var lista: List<FotoItem>
) : RecyclerView.Adapter<FotosMascotaAdapter.ViewHolder>() {

    // Modelo interno para manejar URL o drawable genérico
    data class FotoItem(
        val url: String? = null,
        val drawableRes: Int? = null
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fotos_mascota, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        val ctx  = holder.itemView.context

        if (!item.url.isNullOrEmpty()) {
            Glide.with(ctx)
                .load(item.url)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.img)
        } else if (item.drawableRes != null) {
            holder.img.setImageResource(item.drawableRes)
            holder.img.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Fotos reales desde la API
    fun actualizar(fotos: List<ImagenMascota>) {
        // Repetir cada foto para llenar la grilla visualmente
        val items = mutableListOf<FotoItem>()
        fotos.forEach { foto ->
            repeat(4) { items.add(FotoItem(url = foto.urlImagen)) }
        }
        lista = items
        notifyDataSetChanged()
    }

    // Sin fotos reales — mostrar la genérica repetida
    fun actualizarConGenerica(urlPrincipal: String?, drawableRes: Int) {
        lista = if (!urlPrincipal.isNullOrEmpty()) {
            List(6) { FotoItem(url = urlPrincipal) }
        } else {
            List(6) { FotoItem(drawableRes = drawableRes) }
        }
        notifyDataSetChanged()
    }
}