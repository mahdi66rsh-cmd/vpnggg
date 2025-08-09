package com.godvpn.nativehello;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import androidx.core.app.NotificationCompat;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyVpnService extends VpnService implements Runnable {
    public static final String ACTION_START="START", ACTION_STOP="STOP";
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);
    public static boolean isRunning(){ return RUNNING.get(); }
    private Thread t; private ParcelFileDescriptor tun;

    @Override public int onStartCommand(Intent i, int f, int id) {
        String a = i!=null? i.getAction():null;
        if (ACTION_STOP.equals(a)) { stopAll(); return START_NOT_STICKY; }
        if (!RUNNING.get()) startAll();
        return START_STICKY;
    }
    private void startAll() {
        try {
            startForeground(1, notif("Connecting..."));
            Builder b = new Builder()
                .setSession("GOD OF VPN — Demo")
                .addAddress("10.0.0.2", 32)
                .addDnsServer("1.1.1.1")
                .addRoute("0.0.0.0", 0);
            tun = b.establish();
            RUNNING.set(true);
            t = new Thread(this, "DemoTun"); t.start();
            update("Connected");
        } catch (Exception e) { stopSelf(); }
    }
    @Override public void run() {
        try (FileInputStream in = new FileInputStream(tun.getFileDescriptor());
             FileOutputStream out = new FileOutputStream(tun.getFileDescriptor())) {
            byte[] buf = new byte[32767];
            while (RUNNING.get()) {
                int n = in.read(buf);
                if (n>0) {/* drop */}
            }
        } catch (IOException ignored) {}
        stopAll();
    }
    private void stopAll() {
        RUNNING.set(false);
        if (t!=null){ t.interrupt(); t=null; }
        if (tun!=null) try { tun.close(); } catch (IOException ignored) {}
        stopForeground(true); stopSelf();
    }
    private Notification notif(String txt){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(
            new NotificationChannel("demo","GOD OF VPN", NotificationManager.IMPORTANCE_LOW));
        PendingIntent pi = PendingIntent.getActivity(this,0,
            new Intent(this, MainActivity.class),
            Build.VERSION.SDK_INT>=23? PendingIntent.FLAG_IMMUTABLE:0);
        return new NotificationCompat.Builder(this,"demo")
            .setSmallIcon(android.R.drawable.stat_sys_vpn_ic)
            .setOngoing(true).setContentTitle("GOD OF VPN").setContentText(txt)
            .setContentIntent(pi).build();
    }
    private void update(String t){
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, notif(t));
    }
}
