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
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class formSpecie : AppCompatActivity() {
    //initialisation des variables
    private lateinit var Edt_nom_espece : EditText
    private lateinit var spinner : Spinner
    private lateinit var Edt_population : EditText
    private lateinit var Edt_latitude : TextView
    private lateinit var Edt_longitude : TextView
    private lateinit var btn_convert : Button
    private lateinit var Edt_adresse : EditText
    private lateinit var Edt_temperature_max : EditText
    private lateinit var Edt_temperature_min : EditText
    private lateinit var Edt_humidite_max : EditText
    private lateinit var Edt_humidite_min : EditText
    private lateinit var Edt_notes_observation : EditText
    private lateinit var save_button : Button
    //fin de l'initialisation des variables
    private lateinit var specieDAO: specieDAO
    //toolbar
    private lateinit var toolbar_form_specie : androidx.appcompat.widget.Toolbar
    companion object {
        // Constante pour le code de demande de permission
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_specie)

        //manipulation du toolbar
        toolbar_form_specie = findViewById(R.id.toolbar_form_specie)
        setSupportActionBar(toolbar_form_specie)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.toolbar);
        //fin de la manipulation du toolbar

        //recuperation des vues
        Edt_nom_espece = findViewById(R.id.Edt_nom_espece)
        Edt_notes_observation = findViewById(R.id.Edt_notes_observation)
        spinner = findViewById(R.id.spinner)
        Edt_population = findViewById(R.id.Edt_population)
        Edt_latitude = findViewById(R.id.Edt_latitude)
        Edt_longitude = findViewById(R.id.Edt_longitude)
        btn_convert = findViewById(R.id.btn_convert)
        Edt_adresse = findViewById(R.id.Edt_adresse)
        Edt_temperature_max = findViewById(R.id.Edt_temperature_max)
        Edt_temperature_min = findViewById(R.id.Edt_temperature_min)
        Edt_humidite_max = findViewById(R.id.Edt_humidite_max)
        Edt_humidite_min = findViewById(R.id.Edt_humidite_min)
        save_button = findViewById(R.id.save_button)
        //fin de recuperation des vues
        specieDAO = specieDAO(this)
        //initialisation du toolbar

        //sauvegarde des données dans la base de données
        save_button.setOnClickListener {
            val nomEspece = Edt_nom_espece.text.toString()
            val notesObservation = Edt_notes_observation.text.toString()
            val status = spinner.selectedItem.toString()
            val population = Edt_population.text.toString().toDoubleOrNull() ?: 0.0
            val latitude = Edt_latitude.text.toString()
            val longitude = Edt_longitude.text.toString()
            val adresse = Edt_adresse.text.toString()
            val temperatureMax = Edt_temperature_max.text.toString().toDoubleOrNull() ?: 0.0
            val temperatureMin = Edt_temperature_min.text.toString().toDoubleOrNull() ?: 0.0
            val humiditeMin = Edt_humidite_min.text.toString().toDoubleOrNull() ?: 0.0
            val humiditeMax = Edt_humidite_max.text.toString().toDoubleOrNull() ?: 0.0

            if (nomEspece.isNotEmpty() && notesObservation.isNotEmpty() && status.isNotEmpty() &&
                latitude.isNotEmpty() && longitude.isNotEmpty()&& adresse.isNotEmpty()
            ) {

                val oneSpecie = Specie(
                    name = nomEspece,
                    status = status,
                    population = population,
                    temperature_max = temperatureMax,
                    temperature_min = temperatureMin,
                    humidity_min = humiditeMin,
                    humidity_max = humiditeMax,
                    latitude = latitude,
                    longitude = longitude,
                    description = notesObservation
                )

                // Insertion dans Firebase
                val firebaseDb = FirebaseDatabase.getInstance().getReference("species")
                val newSpecieRef = firebaseDb.push() // Génère un nouvel ID unique
                val specieWithFirebaseId =
                    oneSpecie.copy(id = newSpecieRef.key ?: "") // Utilise l'ID généré
                // Enregistrement dans Firebase
                newSpecieRef.setValue(specieWithFirebaseId)
                    .addOnSuccessListener {
                        // Insertion dans SQLite
                        specieDAO.insertSpecie(specieWithFirebaseId)
                        Toast.makeText(
                            this,
                            getString(R.string.ajoutReussi),
                            Toast.LENGTH_SHORT
                        ).show()
                        // Retour à l'activité de la liste des espèces après l'ajout
                        val intent = Intent(this, species::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(this)
                            .setTitle(getString(R.string.error_title))
                            .setMessage(getString(R.string.error_message_firebase_addition_failed))
                            .setPositiveButton("OK", null)
                            .show()
                    }
            } else {
                // Afficher un message d'erreur si des champs sont vides
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.error_title))
                builder.setMessage(getString(R.string.error_message_invalid_values))
                builder.setNeutralButton("OK", null)
                builder.show()
            }
        }

        // Définir un écouteur de clic pour le bouton de conversion d'adresse en coordonnées
        btn_convert.setOnClickListener {
            val adresse = Edt_adresse.text.toString()
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
                        REQUEST_LOCATION_PERMISSION)
                } else {
                    // Obtention des coordonnées à partir de l'adresse
                    obtenirCoordonneesDepuisAdresse(adresse)
                }
            }
        }


    }
    // Fonction pour obtenir les coordonnées GPS à partir de l'adresse
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun obtenirCoordonneesDepuisAdresse(adresse: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        // Utilisation de la méthode asynchrone pour éviter de bloquer le thread principal
        geocoder.getFromLocationName(adresse, 1, @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
            override fun onGeocode(results: List<Address>) {
                if (results.isNotEmpty()) {
                    val location = results[0]
                    // Exécution sur le thread principal pour mettre à jour l'interface utilisateur
                    runOnUiThread {
                        Edt_latitude.text = "${location.latitude}"
                        Edt_longitude.text = "${location.longitude}"
                    }
                } else {
                    // Si aucune adresse n'est trouvée
                    runOnUiThread {
                        val builder = AlertDialog.Builder(this@formSpecie)
                        builder.setTitle(getString(R.string.error_title))
                        builder.setMessage(getString(R.string.AdresseTrouve))
                        builder.setNeutralButton("OK", null)
                        builder.show()
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                // Gestion des erreurs de géocodage
                runOnUiThread {
                    val builder = AlertDialog.Builder(this@formSpecie)
                    builder.setTitle(getString(R.string.error_title))
                    builder.setMessage("$errorMessage")
                    builder.setNeutralButton("OK", null)
                    builder.show()
                }
            }
        })
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

    // Gestion des résultats de la demande de permission
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Vérifie si la permission de localisation a été accordée
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si la permission est accordée, on obtient les coordonnées depuis l'adresse
                val adresse = Edt_adresse.text.toString()
                obtenirCoordonneesDepuisAdresse(adresse)
            } else {
                // Si la permission est refusée
                Toast.makeText(this, getString(R.string.Permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

}