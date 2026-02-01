package com.example.locationalarm; // וודא שזה תואם לשם החבילה שלך

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // המשתנה שמדבר עם פיירבייס
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. אתחול של פיירבייס
        mAuth = FirebaseAuth.getInstance();

        // קישור לרכיבים במסך
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        Button btnNext = findViewById(R.id.btnLoginNext);
        Button btnBack = findViewById(R.id.btnBackFromLogin);

        // כפתור חזור
        btnBack.setOnClickListener(v -> finish());

        // כפתור התחברות
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // בדיקות תקינות (שלא השאירו שדות ריקים)
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("נא להזין אימייל");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("נא להזין סיסמה");
                    return;
                }

                // 2. הפקודה שבודקת מול פיירבייס
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // הצלחה! המשתמש קיים והסיסמה נכונה
                            Toast.makeText(LoginActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

                            // כאן בדרך כלל עוברים למסך הראשי של האפליקציה
                            // Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                            // startActivity(intent);
                            // finish();
                        } else {
                            // כישלון! (סיסמה לא נכונה או משתמש לא קיים)
                            Toast.makeText(LoginActivity.this, "שגיאה בהתחברות: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}