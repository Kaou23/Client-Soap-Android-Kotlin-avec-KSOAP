package ma.projet.soapclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.databinding.ItemBinding
import java.text.SimpleDateFormat
import java.util.*

class CompteAdapter(
    private var comptes: List<Compte>,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class CompteViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(compte: Compte) {
            binding.txtId.text = "ID: ${compte.id ?: "N/A"}"
            binding.txtSolde.text = "Solde: ${compte.solde}"
            binding.txtType.text = "Type: ${compte.type}"
            binding.txtDate.text = "Date: ${sdf.format(compte.dateCreation)}"
            
            binding.btnDelete.setOnClickListener {
                compte.id?.let { onDeleteClick(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    override fun getItemCount(): Int = comptes.size

    fun updateList(newComptes: List<Compte>) {
        comptes = newComptes
        notifyDataSetChanged()
    }
}
