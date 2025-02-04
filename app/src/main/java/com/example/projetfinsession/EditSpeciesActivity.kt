package com.example.projetfinsession

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetfinsession.formSpecie.Companion
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class EditSpeciesActivity : AppCompatActivity() {
    private lateinit var firebaseDb: DatabaseReference
    private lateinit var modifier_Edt_nom_espece : EditText
    private lateinit var modifier_spinner : Spinner
    private lateinit var modifier_Edt_latitude : TextView
    private lateinit var modifier_Edt_population : EditText
    private lateinit var modifier_Edt_longitude :TextView
    private lateinit var modifier_Edt_adresse : EditText
    private lateinit var modifier_Edt_temperature_max : EditText
    private lateinit var modifier_Edt_temperature_min :EditText
    private lateinit var modifier_Edt_humidite_max : EditText
    private lateinit var modifier_Edt_humidite_min : EditText
    private lateinit var modifier_Edt_notes_observation : EditText
    private lateinit var modifier_button: Button
    private lateinit var modifier_btn_convert: Button
    private lateinit var clickedSpecie: Specie
    private lateinit var specieDAO: specieDAO
    //toolbar
    private lateinit var toolbar3 : androidx.appcompat.widget.Toolbar
    companion object {
        // Constante pour le code de demande de permission
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_species)
        //manipulation du toolbar
        toolbar3= findViewById(R.id.toolbar3)
        setSupportActionBar(toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Modifier l'espèce"
        //fin de la manipulation du toolbar
        //recuperation des vues
        modifier_Edt_nom_espece = findViewById(R.id.modifier_Edt_nom_espece)
        modifier_spinner = findViewById(R.id.modifier_spinner)
        modifier_Edt_latitude = findViewById(R.id.modifier_Edt_latitude)
        modifier_Edt_population = findViewById(R.id.modifier_Edt_population)
        modifier_Edt_longitude = findViewById(R.id.modifier_Edt_longitude)
        modifier_Edt_adresse = findViewById(R.id.modifier_Edt_adresse)
        modifier_Edt_temperature_max = findViewById(R.id.modifier_Edt_temperature_max)
        modifier_Edt_temperature_min = findViewById(R.id.modifier_Edt_temperature_min)
        modifier_Edt_humidite_max = findViewById(R.id.modifier_Edt_humidite_max)
        modifier_Edt_humidite_min = findViewById(R.id.modifier_Edt_humidite_min)
        modifier_Edt_notes_observation = findViewById(R.id.modifier_Edt_notes_observation)
        modifier_button = findViewById(R.id.modifier_button)
        modifier_btn_convert = findViewById(R.id.modifier_btn_convert)
        //initialisation de SqlLite et FireBase
        specieDAO = specieDAO(this)
        firebaseDb = FirebaseDatabase.getInstance().getReference("species")

        val speciesId = intent.getStringExtra("species_id")
        if (speciesId != null) {
            // Récupérer les informations de l'animal depuis sonID
            clickedSpecie = specieDAO.getSpecieById(speciesId)
            if (clickedSpecie != null) {
                //Remplir les champs de saisie avec le specimen trouvé
                modifier_Edt_nom_espece.setText(clickedSpecie.name)
                modifier_Edt_latitude.setText(clickedSpecie.latitude)
                modifier_spinner.setSelection(getSpinnerIndex(modifier_spinner, clickedSpecie.status))
                modifier_Edt_population.setText(clickedSpecie.population.toString())
                modifier_Edt_longitude.setText(clickedSpecie.longitude)
                modifier_Edt_temperature_max.setText(clickedSpecie.temperature_max.toString())
                modifier_Edt_temperature_min.setText(clickedSpecie.temperature_min.toString())
                modifier_Edt_humidite_max.setText(clickedSpecie.humidity_max.toString())
                modifier_Edt_humidite_min.setText(clickedSpecie.humidity_min.toString())
                modifier_Edt_notes_observation.setText(clickedSpecie.description)
            }
        }

        // Gestion du clic sur le bouton de sauvegarde
        modifier_button.setOnClickListener {
            // Mettre à jour dans Firebase
            // Récupérer les valeurs saisies par l'utilisateur
            val name = modifier_Edt_nom_espece.text.toString()
            val status = modifier_spinner.selectedItem.toString()
            val latitude = modifier_Edt_latitude.text.toString()
            val longitude = modifier_Edt_longitude.text.toString()
            val population = modifier_Edt_population.text.toString().toDoubleOrNull() ?: 0.0
            val temperatureMax = modifier_Edt_temperature_max.text.toString().toDoubleOrNull() ?: 0.0
            val temperatureMin = modifier_Edt_temperature_min.text.toString().toDoubleOrNull() ?: 0.0
            val humidityMax = modifier_Edt_humidite_max.text.toString().toDoubleOrNull() ?: 0.0
            val humidityMin = modifier_Edt_humidite_min.text.toString().toDoubleOrNull() ?: 0.0
            val description = modifier_Edt_notes_observation.text.toString()

            // Vérifier si des champs essentiels sont vides
            if (name.isEmpty() || status.isEmpty() || latitude.isEmpty() || longitude.isEmpty() || description.isEmpty()) {
                // Afficher un message d'erreur ou alerter l'utilisateur
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Erreur")
                builder.setMessage("Veuillez remplir tous les champs obligatoires")
                builder.setNeutralButton("OK", null)
                builder.show()
            } else {
                // Vérifier si les valeurs numériques sont valides, par exemple, la population et la température
                if (population <= 0 || temperatureMax <= 0 || temperatureMin <= 0 || humidityMax <= 0 || humidityMin <= 0) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Erreur")
                    builder.setMessage("Veuillez entrer des valeurs valides pour les champs numériques")
                    builder.setNeutralButton("OK", null)
                    builder.show()
                } else {
                    // Mettre à jour dans SQLite
                    clickedSpecie.name = name
                    clickedSpecie.status = status
                    clickedSpecie.latitude = latitude
                    clickedSpecie.longitude = longitude
                    clickedSpecie.population = population
                    clickedSpecie.temperature_max = temperatureMax
                    clickedSpecie.temperature_min = temperatureMin
                    clickedSpecie.humidity_max = humidityMax
                    clickedSpecie.humidity_min = humidityMin
                    clickedSpecie.description = description

                    specieDAO.updateSpecie(clickedSpecie)

                    // Mettre à jour dans Firebase
                    // Exemple d'utilisation de FirebaseDatabase pour la mise à jour (adaptez selon votre structure Firebase)
//                    val database = FirebaseDatabase.getInstance()
//                    val reference = database.getReference("species").child(clickedSpecie.id.toString())
//                    reference.setValue(clickedSpecie)

                    // Lancer un intent pour une autre activité, si nécessaire
                    val intent = Intent(this, species::class.java)
                    startActivity(intent)
                }
            }

        }
        modifier_btn_convert.setOnClickListener{
            val adresse = modifier_Edt_adresse.text.toString()
            if (adresse.isNotEmpty()) {
                // Vérification de la permission de localisation
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    // Demande des permissions de localisation si elles ne sont pas déjà accordées
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                            REQUEST_LOCATION_PERMISSION
                    )
                } else {
                    // Obtention des coordonnées à partir de l'adresse
                    modifierCoordonneesDepuisAdresse(adresse)
                }
            }

        }

    }
    // Fonction pour obtenir les coordonnées GPS à partir de l'adresse
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun modifierCoordonneesDepuisAdresse(adresse: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        // Utilisation de la méthode asynchrone pour éviter de bloquer le thread principal
        geocoder.getFromLocationName(adresse, 1, @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
            override fun onGeocode(results: List<Address>) {
                if (results.isNotEmpty()) {
                    val location = results[0]
                    // Exécution sur le thread principal pour mettre à jour l'interface utilisateur
                    runOnUiThread {
                        modifier_Edt_latitude.text = "${location.latitude}"
                        modifier_Edt_longitude.text = "${location.longitude}"
                    }
                } else {
                    // Si aucune adresse n'est trouvée
                    runOnUiThread {
                        val builder = AlertDialog.Builder(this@EditSpeciesActivity)
                        builder.setTitle("Erreur")
                        builder.setMessage("Aucun résultat trouvé pour l'adresse")
                        builder.setNeutralButton("OK", null)
                        builder.show()
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                // Gestion des erreurs de géocodage
                runOnUiThread {
                    val builder = AlertDialog.Builder(this@EditSpeciesActivity)
                    builder.setTitle("Erreur")
                    builder.setMessage("$errorMessage")
                    builder.setNeutralButton("OK", null)
                    builder.show()
                }
            }
        })
    }

    // Fonction pour définir la sélection dans le spinner
    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }
    // Gestion des résultats de la demande de permission
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Vérifie si la permission de localisation a été accordée
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si la permission est accordée, on obtient les coordonnées depuis l'adresse
                val adresse = modifier_Edt_adresse.text.toString()
                modifierCoordonneesDepuisAdresse(adresse)
            } else {
                // Si la permission est refusée
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}