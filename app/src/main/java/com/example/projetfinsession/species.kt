package com.example.projetfinsession

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class species : AppCompatActivity() {
    //déclaration des variables
    private lateinit var speciesRecyclerView : RecyclerView
    private lateinit var speciesAdapter: SpeciesAdapter
    private lateinit var floatingBtnAdd: FloatingActionButton
    private lateinit var txtTempAmbiante : TextView
    private lateinit var txtHumidAmbiante : TextView
     private  var speciesList =  mutableListOf<Specie>()
    //Fin de la déclaration des variables
    private lateinit var specieDAO: specieDAO
    //initialisation des sensors
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor?= null
    private var humiditySensor: Sensor?= null
    private lateinit var sensorEventListener:SensorEventListener
    //initialisation de la DB
    private lateinit var firebaseDb: DatabaseReference // Référence Firebase pour stocker les tâches dans le cloud
    //initialisation des temperatures et humidités max et min
    private var MAX_TEMPERATURE = 70
    private var MIN_TEMPERATURE = 35
    private var MAX_HUMIDITY = 70
    private var MIN_HUMIDITY = 20
    // Initialisation paresseuse de NotificationHelper
    private val notificationHelper by lazy { NotificationHelper(this) }
    private val REQUEST_NOTIFICATION_PERMISSION = 1001 // Code de requête pour les permissions
    //notificatioID
    val TEMPERATURE_NOTIFICATION_ID = 1
    val HUMIDITY_NOTIFICATION_ID = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_species)

        // Initialiser le Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title =getString(R.string.species_title);


        //Initialisation du gestionnaire de capteurs
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkNotificationPermission()) {
                registerTemperatureSensor() // Enregistre le capteur si la permission est accordée
                registerHumiditySensor()
            } else {
                requestNotificationPermission() // Demande la permission de notification
            }
        } else {
            registerTemperatureSensor() // Enregistre le capteur pour les versions d'Android inférieures
            registerHumiditySensor()
        }

        //obtention des capteurs de temperature et d'humidité
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        if(humiditySensor == null){
            Toast.makeText(this, getString(R.string.error_humidity_sensor_unavailable), Toast.LENGTH_SHORT).show()
        }
        if(temperatureSensor == null){
            Toast.makeText(this,  getString(R.string.error_temperature_sensor_unavailable), Toast.LENGTH_SHORT).show()
        }
        // Si aucun capteur n'est disponible, fin de l'activité
        if (humiditySensor == null && temperatureSensor == null) {
            Toast.makeText(this, getString(R.string.error_no_sensors_available), Toast.LENGTH_LONG).show()
            //finish()
        }

        //definittion du listener pour les evenements des capteurs
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                //verification du type de capteurs
                when (event?.sensor?.type) {
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        val temperature = event.values[0]
                        txtTempAmbiante.text = "${temperature}°C"

                        // Récupère la liste des espèces et vérifie les seuils
                        speciesList.forEach { species ->
                            if (temperature > species.temperature_max) {
                                notificationHelper.sendUrgentNotification(
                                    TEMPERATURE_NOTIFICATION_ID,
                                    getString(R.string.alert_temperature_high)+" ${species.name}",
                                    getString(R.string.alert_temperature_high)+": ${species.name} : ${temperature}°C"
                                )
                            } else if (temperature < species.temperature_min) {
                                notificationHelper.sendUrgentNotification(
                                    TEMPERATURE_NOTIFICATION_ID,
                                    getString(R.string.alert_temperature_low)+" ${species.name}",
                                    getString(R.string.alert_temperature_low)+": ${temperature}°C"
                                )
                            }
                        }
                    }

                    Sensor.TYPE_RELATIVE_HUMIDITY -> {
                        val humidity = event.values[0]
                        txtHumidAmbiante.text = "${humidity}%"

                        // Vérifie les seuils d'humidité pour chaque espèce
                        speciesList.forEach { species ->
                            if (humidity > species.humidity_max) {
                                notificationHelper.sendUrgentNotification(
                                    HUMIDITY_NOTIFICATION_ID,
                                    getString(R.string.alert_humidity_high)+" ${species.name}",
                                    getString(R.string.alert_humidity_high)+ ": ${species.name} : ${humidity}%"
                                )
                            } else if (humidity < species.humidity_min) {
                                notificationHelper.sendUrgentNotification(
                                    HUMIDITY_NOTIFICATION_ID,
                                    getString(R.string.alert_humidity_low)+" ${species.name}",
                                    getString(R.string.alert_humidity_low)+  ": ${species.name} ${humidity}%"
                                )
                            }
                        }
                    }
                }

            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        //Récupération des éléments des vues
        speciesRecyclerView = findViewById(R.id.speciesRecyclerView)
        floatingBtnAdd = findViewById(R.id.addSpeciesFab)
        txtTempAmbiante = findViewById(R.id.txtTempAmbiante)
        txtHumidAmbiante = findViewById(R.id.txtHumidAmbiante)

        //recuperation des specimens dans la BD
        try {
            //initialisation de la DB dans SqlLite
            specieDAO = specieDAO(this)
            speciesList.clear()
            speciesList.addAll(specieDAO.getAllSpecies())
            speciesAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Affichage des données dans le recyclerview
        val layoutManager = GridLayoutManager(this, 2)
        speciesAdapter = SpeciesAdapter(this,speciesList)
        speciesRecyclerView.adapter = speciesAdapter
        speciesRecyclerView.layoutManager = layoutManager
        //Fin de l'affichage des données dans le recyclerview

        //Gestion du clic sur le bouton pour ouvrir la page d'ajout
        floatingBtnAdd.setOnClickListener {
            val intent = Intent(this,formSpecie::class.java)
            startActivity(intent)
        }
        //Fin de la gestion du clic sur le bouton pour ouvrir la page d'ajout
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                registerTemperatureSensor() // Enregistre le capteur si la permission est accordée
                registerHumiditySensor()
            } else {
                // Permission refusée, gérer en conséquence
                Toast.makeText(this,  getString(R.string.error_notification_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerTemperatureSensor() {
        temperatureSensor?.let {
            sensorManager.registerListener(sensorEventListener,it,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    private fun registerHumiditySensor(){
        humiditySensor?.let {
            sensorManager.registerListener(sensorEventListener,it,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        //enregistrement du capteurs pour gagner des performances
        registerTemperatureSensor()
        registerHumiditySensor()
    }

    override fun onPause() {
        super.onPause()
        //Desenregistrement du capteurs pours gagner des performances
        sensorManager.unregisterListener(sensorEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Ajouter l'icône au Toolbar
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Gérer le clic sur l'icône
        return when (item.itemId) {
            R.id.action_open_activity -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showSpeciesOptionsDialog(species: Specie) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.species_details_title))
        builder.setMessage(
            getString(R.string.name)+":  ${species.name}\n" +
                    getString(R.string.status)+":  ${species.status}\n" +
                    "Population: ${species.population}\n" +
                    getString(R.string.Mrequired)+": ${species.temperature_max}\n" +
                    getString(R.string.mrequired)+":  ${species.temperature_min}\n" +
                    getString(R.string.MHrequired)+":   ${species.humidity_max}\n" +
                    getString(R.string.mHrequired)+":  ${species.humidity_min}\n" +
                    getString(R.string.Notes)+":  ${species.description}"
        )

        builder.setPositiveButton("Modifier") { _, _ ->
            // Action pour modifier l'espèce
            val intent = Intent(this, EditSpeciesActivity::class.java)
            intent.putExtra("species_id", species.id)
            this.startActivity(intent)
        }

        builder.setNegativeButton(getString(R.string.delete)) { _, _ ->
            // Suppression dans SQLite
            species.id?.let { id ->
                // Suppression de l'espèce dans la base de données locale SQLite
                specieDAO.deleteSpecie(id)
                // Suppression de l'espèce dans Firebase
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("species").child(id.toString())
                reference.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si la suppression dans Firebase réussit
                        speciesList.remove(species)
                        speciesAdapter.notifyDataSetChanged()
                        // Afficher un message de confirmation
                        Toast.makeText(this, getString(R.string.species_deleted_successfully), Toast.LENGTH_SHORT).show()
                    } else {
                        // Gestion de l'erreur si la suppression dans Firebase échoue
                        Toast.makeText(this, getString(R.string.species_deleted_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNeutralButton(getString(R.string.cancel), null)
        builder.show()
    }

    // Méthode pour vérifier si la permission de notification est accordée
    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                "android.permission.POST_NOTIFICATIONS"
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pas besoin de vérifier les permissions pour les versions inférieures
        }
    }

    // Méthode pour demander la permission de notification à l'utilisateur
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }
}