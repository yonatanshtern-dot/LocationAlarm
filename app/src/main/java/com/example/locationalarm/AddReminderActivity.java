package com.example.locationalarm;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double selectedLat = 0;
    private double selectedLng = 0;
    private com.google.android.gms.location.GeofencingClient geofencingClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        geofencingClient = com.google.android.gms.location.LocationServices.getGeofencingClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        EditText etReminderDate = findViewById(R.id.etReminderDate);
        etReminderDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddReminderActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                            String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            etReminderDate.setText(dateString);
                        }
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        EditText etReminderTime = findViewById(R.id.etReminderTime);
        etReminderTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AddReminderActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int selectedMinute) {
                            String timeString = String.format("%02d:%02d", hourOfDay, selectedMinute);
                            etReminderTime.setText(timeString);
                        }
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        Button btnSave = findViewById(R.id.btnSaveReminder);
        EditText etTitle = findViewById(R.id.etReminderTitle);
        EditText etDate = findViewById(R.id.etReminderDate);
        EditText etTime = findViewById(R.id.etReminderTime);
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String rawTime = etTime.getText().toString().trim();
            final String time = rawTime.isEmpty() ? "00:00" : rawTime;
            if (title.isEmpty()) {
                etTitle.setError("נא להזין שם תזכורת");
                return;
            }
            if (selectedLat == 0 && selectedLng == 0) {
                Toast.makeText(this, "נא לבחור מיקום על המפה (לחיצה על המפה תשים נעץ)", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
            com.google.firebase.database.DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Reminders").child(uid);
            String reminderId = dbRef.push().getKey();
            Reminder reminder = new Reminder(reminderId, title, date, time, selectedLat, selectedLng);
            dbRef.child(reminderId).setValue(reminder).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "התזכורת נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    Geofence geofence = new Geofence.Builder()
                            .setRequestId(reminderId + "|||" + title + "|||" + date + "|||" + time)
                            .setCircularRegion(selectedLat, selectedLng, 100)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            .build();
                    com.google.android.gms.location.GeofencingRequest geofencingRequest = new com.google.android.gms.location.GeofencingRequest.Builder()
                            .setInitialTrigger(com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER)
                            .addGeofence(geofence)
                            .build();
                    android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                            AddReminderActivity.this,
                            0,
                            new android.content.Intent(AddReminderActivity.this, GeofenceReceiver.class),
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_MUTABLE
                    );
                    if (androidx.core.app.ActivityCompat.checkSelfPermission(AddReminderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        geofencingClient.addGeofences(geofencingRequest, pendingIntent);
                    }
                    finish();
                } else {
                    Toast.makeText(this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng israel = new LatLng(31.0461, 34.8516);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(israel, 8));
        enableMyLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("מיקום התזכורת"));
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;
            }
        });
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true); // מוסיף את הכפתור של ה-GPS למפה
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "האפליקציה חייבת הרשאת מיקום כדי להתמקד עליך", Toast.LENGTH_LONG).show();
            }
        }
    }
}