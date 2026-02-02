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

        // כפתור הוספת תזכורת
        findViewById(R.id.btnNavAddReminder).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AddReminderActivity.class));
        });

        // כפתור תזכורות קיימות
        findViewById(R.id.btnNavShowReminders).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RemindersListActivity.class));
        });

        // כפתור הגדרות
        findViewById(R.id.btnNavSettings).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        });
    }
}