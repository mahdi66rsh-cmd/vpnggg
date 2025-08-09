package com.godvpn.nativehello;

import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private TextView tv; private Button btn;
    private final ActivityResultLauncher<Intent> prep =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r -> startVpn());

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tvStatus);
        btn = findViewById(R.id.btnToggle);

        btn.setOnClickListener(v -> {
            if (MyVpnService.isRunning()) stopVpn();
            else prepareAndStart();
        });
        update();
    }
    private void prepareAndStart() {
        Intent i = VpnService.prepare(this);
        if (i != null) prep.launch(i); else startVpn();
    }
    private void startVpn() {
        Intent svc = new Intent(this, MyVpnService.class).setAction(MyVpnService.ACTION_START);
        if (Build.VERSION.SDK_INT >= 26) ContextCompat.startForegroundService(this, svc);
        else startService(svc);
        update();
    }
    private void stopVpn() {
        Intent svc = new Intent(this, MyVpnService.class).setAction(MyVpnService.ACTION_STOP);
        startService(svc); update();
    }
    private void update() {
        boolean on = MyVpnService.isRunning();
        tv.setText(on ? "Status: CONNECTED" : "Status: DISCONNECTED");
        btn.setText(on ? "Stop VPN" : "Start VPN");
    }
}
