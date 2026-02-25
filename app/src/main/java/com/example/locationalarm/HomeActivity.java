package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.cardAddReminder).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AddReminderActivity.class));
        });
        findViewById(R.id.cardViewReminders).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RemindersListActivity.class));
        });
        findViewById(R.id.cardSettings).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        });
    }
}