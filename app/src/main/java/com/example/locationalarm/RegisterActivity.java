package com.example.locationalarm;

import android.content.Intent;
import android.content.SharedPreferences; // 1. הוספנו ייבוא
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox; // 2. הוספנו ייבוא
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etConfirmPass;
    private CheckBox cbRememberMe; // משתנה לתיבת הסימון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPass = findViewById(R.id.etRegisterConfirmPassword);
        cbRememberMe = findViewById(R.id.cbRegisterRememberMe); // קישור ל-XML

        Button btnNext = findViewById(R.id.btnRegisterNext);
        Button btnBack = findViewById(R.id.btnBackFromRegister);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPass = etConfirmPass.getText().toString().trim();

                // בדיקות תקינות
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("נא להזין אימייל");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("נא להזין סיסמה");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("סיסמה חייבת להיות לפחות 6 תווים");
                    return;
                }
                if (!password.equals(confirmPass)) {
                    etConfirmPass.setError("הסיסמאות אינן תואמות");
                    return;
                }

                createFirebaseUser(email, password);
            }
        });
    }

    private void createFirebaseUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // --- החלק החדש: שמירה בזיכרון בהרשמה ---
                            if (cbRememberMe.isChecked()) {
                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                editor.putString("saved_email", email);
                                editor.putBoolean("is_remembered", true);
                                editor.apply();
                            }
                            // ----------------------------------------

                            Toast.makeText(RegisterActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "נכשל: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}