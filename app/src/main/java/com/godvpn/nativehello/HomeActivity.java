package com.godvpn.nativehello;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ((TextView)findViewById(R.id.tvWelcome)).setText("GOD OF VPN");

        findViewById(R.id.btnNativeVpn).setOnClickListener(v ->
            startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btnV2Ray).setOnClickListener(v ->
            startActivity(new Intent(this, V2RayActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            AuthStore.logout(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
