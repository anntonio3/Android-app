package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.Usuario

class UsuarioAdapter(private var listaUsuarios:List<Usuario>, private val onClick: (Usuario) -> Unit) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val txtTelefono: TextView = itemView.findViewById(R.id.txtTelefono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.txtNombre.text = usuario.nombre
        holder.txtEmail.text = usuario.email
        holder.txtTelefono.text = usuario.telefono

        // Asignar click
        holder.itemView.setOnClickListener {
            onClick(usuario)
        }

    }

    fun actualizarLista(nuevaLista: List<Usuario>){
        listaUsuarios = nuevaLista
        notifyDataSetChanged()
    }
}