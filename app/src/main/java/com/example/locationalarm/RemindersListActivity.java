package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RemindersListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // כפתור הפלוס מעביר למסך הוספה
        findViewById(R.id.fabAddReminder).setOnClickListener(v -> {
            startActivity(new Intent(RemindersListActivity.this, AddReminderActivity.class));
        });
    }
}