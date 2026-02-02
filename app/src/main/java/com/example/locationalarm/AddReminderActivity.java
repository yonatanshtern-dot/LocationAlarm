package com.example.locationalarm;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// שינוי 1: הוספנו ייבוא של המפות
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// שינוי 2: הוספנו "implements OnMapReadyCallback"
public class AddReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double selectedLat = 0;
    private double selectedLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // טעינת המפה
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnSave = findViewById(R.id.btnSaveReminder);
        EditText etTitle = findViewById(R.id.etReminderTitle);

        btnSave.setOnClickListener(v -> {
            // בדיקה אם המשתמש בחר מיקום
            if (selectedLat == 0 && selectedLng == 0) {
                Toast.makeText(this, "נא לבחור מיקום על המפה", Toast.LENGTH_SHORT).show();
                return;
            }

            // שמירה (דמה בינתיים)
            Toast.makeText(this, "נשמר במיקום: " + selectedLat + ", " + selectedLng, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    // הפונקציה הזו נקראת כשהמפה מוכנה
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // מיקום ברירת מחדל (ישראל) כדי שהמפה לא תתחיל בים
        LatLng israel = new LatLng(31.0461, 34.8516);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(israel, 8));

        // מאזין ללחיצה על המפה
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 1. ניקוי נעצים קודמים
                mMap.clear();

                // 2. הוספת נעץ חדש איפה שהמשתמש לחץ
                mMap.addMarker(new MarkerOptions().position(latLng).title("מיקום נבחר"));

                // 3. שמירת הקואורדינטות למשתנים
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;
            }
        });
    }
}