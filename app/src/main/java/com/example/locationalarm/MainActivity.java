package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        // בדיקה האם המשתמש כבר מחובר
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // המשתמש מחובר -> דלג ישר למסך הבית
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // סגירת המסך הנוכחי
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMoveToRegister = findViewById(R.id.btnMoveToRegister);
        Button btnMoveToLogin = findViewById(R.id.btnMoveToLogin);

        btnMoveToRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        btnMoveToLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}