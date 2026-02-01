package com.example.locationalarm;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// אלו השורות הקריטיות שחסרות לך כנראה:
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    // 1. משתנה לחיבור לפיירבייס
    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword, etConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 2. אתחול פיירבייס
        mAuth = FirebaseAuth.getInstance();

        // קישור לרכיבים במסך
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPass = findViewById(R.id.etRegisterConfirmPassword);
        Button btnNext = findViewById(R.id.btnRegisterNext);
        Button btnBack = findViewById(R.id.btnBackFromRegister);

        btnBack.setOnClickListener(v -> finish());

        // לוגיקת ההרשמה
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPass = etConfirmPass.getText().toString().trim();

                // בדיקות תקינות בסיסיות
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

                // 3. יצירת המשתמש בפיירבייס
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
                            // הצלחה! המשתמש נוצר
                            Toast.makeText(RegisterActivity.this, "ההרשמה הצליחה!", Toast.LENGTH_SHORT).show();

                            // כאן אפשר לעבור למסך הבא או לסגור את ההרשמה
                            finish();
                        } else {
                            // כישלון
                            Toast.makeText(RegisterActivity.this, "נכשל: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}