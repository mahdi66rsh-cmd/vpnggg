package com.godvpn.nativehello;

import android.Manifest;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class V2RayActivity extends AppCompatActivity {
    private static final String VLESS_LINK =
        "vless://e3075864-27df-4d15-b181-9c94b0e6a53c@216.9.224.59:443?path=%2FvTlFuPRGGIPbezDZVgy1yX&security=tls&alpn=http%2F1.1&encryption=none&host=onlinebazikon.ir&fp=chrome&type=httpupgrade&sni=onlinebazikon.ir#onlinebazikon.ir%20tls%20httpupgrade%20direct%20vless";

    private TextView tv; private Button btnC, btnD;
    private final ActivityResultLauncher<Intent> prep =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r -> startCore());

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_v2ray);
        tv = findViewById(R.id.tvV2Status);
        btnC = findViewById(R.id.btnV2Connect);
        btnD = findViewById(R.id.btnV2Disconnect);

        btnC.setOnClickListener(v -> {
            Intent i = VpnService.prepare(this);
            if (i != null) prep.launch(i); else startCore();
        });
        btnD.setOnClickListener(v -> stopCore());

        if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        update();
    }
    private void startCore() {
        Intent s = new Intent(this, XrayTunnelService.class)
            .setAction(XrayTunnelService.ACTION_START)
            .putExtra("vless", VLESS_LINK);
        if (Build.VERSION.SDK_INT >= 26) ContextCompat.startForegroundService(this, s);
        else startService(s);
        Toast.makeText(this, "در حال اتصال...", Toast.LENGTH_SHORT).show();
        update();
    }
    private void stopCore() {
        Intent s = new Intent(this, XrayTunnelService.class).setAction(XrayTunnelService.ACTION_STOP);
        startService(s); update();
    }
    private void update() {
        tv.setText(XrayTunnelService.isRunning() ? "V2Ray: CONNECTED" : "V2Ray: DISCONNECTED");
    }
}
