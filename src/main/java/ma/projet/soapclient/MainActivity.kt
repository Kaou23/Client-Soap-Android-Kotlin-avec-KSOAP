package ma.projet.soapclient

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.projet.soapclient.adapter.CompteAdapter
import ma.projet.soapclient.databinding.ActivityMainBinding
import ma.projet.soapclient.databinding.PopupBinding
import ma.projet.soapclient.ws.Service

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val service = Service()
    private lateinit var adapter: CompteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadComptes()

        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CompteAdapter(emptyList()) { id ->
            deleteCompte(id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadComptes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = service.getComptes()
            withContext(Dispatchers.Main) {
                adapter.updateList(list)
            }
        }
    }

    private fun showAddDialog() {
        val popupBinding = PopupBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setTitle("Ajouter un compte")
            .setView(popupBinding.root)
            .setPositiveButton("Ajouter") { _, _ ->
                val solde = popupBinding.editSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (popupBinding.radioCourant.isChecked) "COURANT" else "EPARGNE"
                addCompte(solde, type)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun addCompte(solde: Double, type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val success = service.createCompte(solde, type)
            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@MainActivity, "Compte ajouté", Toast.LENGTH_SHORT).show()
                    loadComptes()
                } else {
                    Toast.makeText(this@MainActivity, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCompte(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val success = service.deleteCompte(id)
            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@MainActivity, "Compte supprimé", Toast.LENGTH_SHORT).show()
                    loadComptes()
                } else {
                    Toast.makeText(this@MainActivity, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
