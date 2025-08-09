package com.godvpn.nativehello;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String[][] USERS = {
        {"user1","pass1"},{"user2","pass2"},{"user3","pass3"},{"user4","pass4"},{"user5","pass5"},
        {"user6","pass6"},{"user7","pass7"},{"user8","pass8"},{"user9","pass9"},{"user10","pass10"}
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthStore.isLoggedIn(this)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish(); return;
        }

        setContentView(R.layout.activity_login);
        EditText etUser = findViewById(R.id.etUser);
        EditText etPass = findViewById(R.id.etPass);
        Button btn = findViewById(R.id.btnLogin);

        btn.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString().trim();
            boolean ok = false;
            for (String[] pair : USERS) if (pair[0].equals(u) && pair[1].equals(p)) { ok = true; break; }
            if (ok) {
                AuthStore.setLoggedIn(this, true);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "نام‌کاربری/رمز اشتباه است", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
