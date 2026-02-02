package com.example.locationalarm;

import android.content.Intent;
import android.content.SharedPreferences; // ייבוא חדש
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox; // ייבוא חדש
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe; // משתנה לתיבת הסימון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        cbRememberMe = findViewById(R.id.cbLoginRememberMe); // קישור ל-xml
        Button btnNext = findViewById(R.id.btnLoginNext);
        Button btnBack = findViewById(R.id.btnBackFromLogin);

        // --- החלק החדש: טעינת האימייל השמור (אם יש) ---
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("saved_email", ""); // שליפת האימייל
        boolean isRemembered = prefs.getBoolean("is_remembered", false);

        if (isRemembered) {
            etEmail.setText(savedEmail);
            cbRememberMe.setChecked(true);
        }
        // ---------------------------------------------

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("נא להזין אימייל");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("נא להזין סיסמה");
                    return;
                }

                // --- שמירת האימייל בזיכרון אם התיבה מסומנת ---
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                if (cbRememberMe.isChecked()) {
                    editor.putString("saved_email", email);
                    editor.putBoolean("is_remembered", true);
                } else {
                    // אם המשתמש ביטל את הסימון, נמחק את הזיכרון
                    editor.remove("saved_email");
                    editor.putBoolean("is_remembered", false);
                }
                editor.apply(); // ביצוע השמירה
                // ---------------------------------------------

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
                            Toast.makeText(LoginActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
