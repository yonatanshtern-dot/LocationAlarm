package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. ביצוע הניתוק מול פיירבייס
                FirebaseAuth.getInstance().signOut();

                // 2. הודעה למשתמש
                Toast.makeText(SettingsActivity.this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

                // 3. מעבר למסך הפתיחה (MainActivity)
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

                // 4. ניקוי ההיסטוריה - כדי שלחיצה על "חזור" לא תחזיר אותו לתוך האפליקציה
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });
    }
}