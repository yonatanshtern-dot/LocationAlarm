package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChangeSound).setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            startActivity(intent);
        });
        findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                com.google.firebase.database.DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Reminders").child(uid);
                dbRef.removeValue().addOnCompleteListener(dbTask -> {
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "החשבון וכל הנתונים נמחקו לצמיתות", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SettingsActivity.this, "שגיאה במחיקת החשבון. ייתכן שיש להתנתק ולהתחבר מחדש קודם.", Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SettingsActivity.this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        Button btnToggleChangePassword = findViewById(R.id.btnToggleChangePassword);
        LinearLayout layoutChangePassword = findViewById(R.id.layoutChangePassword);
        EditText etOldPassword = findViewById(R.id.etOldPassword);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnSaveNewPassword = findViewById(R.id.btnSaveNewPassword);
        btnToggleChangePassword.setOnClickListener(v -> {
            if (layoutChangePassword.getVisibility() == View.GONE) {
                layoutChangePassword.setVisibility(View.VISIBLE);
            } else {
                layoutChangePassword.setVisibility(View.GONE);
            }
        });
        btnSaveNewPassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            if (TextUtils.isEmpty(oldPass)) {
                etOldPassword.setError("נא להזין סיסמה נוכחית");
                return;
            }
            if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
                etNewPassword.setError("סיסמה חדשה חייבת להכיל לפחות 6 תווים");
                return;
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "הסיסמה שונתה בהצלחה!", Toast.LENGTH_SHORT).show();
                                etOldPassword.setText("");
                                etNewPassword.setText("");
                                layoutChangePassword.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(SettingsActivity.this, "שגיאה בעדכון: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        etOldPassword.setError("הסיסמה הנוכחית שגויה");
                    }
                });
            }
        });
    }
}