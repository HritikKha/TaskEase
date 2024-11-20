package com.example.taskmanager


import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class CreateTask : AppCompatActivity() {

    private lateinit var database: myDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var save_button: Button
    private lateinit var create_title: EditText
    private lateinit var priority_group: RadioGroup
    private lateinit var radio_high: RadioButton
    private lateinit var radio_medium: RadioButton
    private lateinit var radio_low: RadioButton
    private lateinit var location_button: Button
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private var selectedTime: String? = null
    private var selectedLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task)

        // Initialize views
        save_button = findViewById(R.id.save_button)
        create_title = findViewById(R.id.create_title)
        priority_group = findViewById(R.id.priority_group)
        radio_high = findViewById(R.id.radio_high)
        radio_medium = findViewById(R.id.radio_medium)
        radio_low = findViewById(R.id.radio_low)
        location_button = findViewById(R.id.location_button)

        // Initialize database
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "Task_Manager"
        ).build()

        // Time picker dialog functionality
        val timeButton: Button = findViewById(R.id.time_button)
        timeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                // Format and display the selected time
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                timeButton.text = "Time: $selectedTime"
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Location button functionality
        location_button.setOnClickListener {
            fetchCurrentLocation()
            selectedLocation = "Sample Location"
            Toast.makeText(this, "Location Selected: $selectedLocation", Toast.LENGTH_SHORT).show()
        }

        // Save button functionality
        save_button.setOnClickListener {
            val title = create_title.text.toString().trim()
            val selectedId = priority_group.checkedRadioButtonId
            val priority = when (selectedId) {
                R.id.radio_high -> "High"
                R.id.radio_medium -> "Medium"
                R.id.radio_low -> "Low"
                else -> ""
            }

            // Validate inputs
            if (title.isNotEmpty() && priority.isNotEmpty() && selectedTime != null && selectedLocation != null) {
                // Set data using DataObject or directly in the database
                DataObject.setData(title, priority, selectedTime!!, selectedLocation!!)

                // Insert task into the database asynchronously using coroutines
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority, selectedTime!!, selectedLocation!!))
                }



                // Show success message and navigate to MainActivity
//                Toast.makeText(this, "Task Saved Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Show error messages for missing fields
                if (title.isEmpty()) create_title.error = "Title is required"
                if (priority.isEmpty()) radio_high.error = "Priority is required"
                if (selectedTime == null) Toast.makeText(this, "Time is required", Toast.LENGTH_SHORT).show()
                if (selectedLocation == null) Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fetchCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                    // Fetch address using Geocoder
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0) // Full address
                        selectedLocation = address
                    } else {
                        Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }




}
