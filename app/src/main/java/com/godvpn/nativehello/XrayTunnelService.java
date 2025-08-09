package com.godvpn.nativehello;

import android.app.*;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import androidx.core.app.NotificationCompat;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class XrayTunnelService extends VpnService implements Runnable {
    public static final String ACTION_START="START", ACTION_STOP="STOP";
    private static final String CH="xray_vpn";
    private static final AtomicBoolean RUNNING=new AtomicBoolean(false);
    public static boolean isRunning(){ return RUNNING.get(); }

    private Thread thread; private ParcelFileDescriptor tun;
    private Process xrayProc, t2sProc; private String vlessLink;

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String act = intent!=null? intent.getAction():null;
        if (ACTION_STOP.equals(act)) { stopAll(); return START_NOT_STICKY; }
        if (!RUNNING.get()) { vlessLink = intent!=null? intent.getStringExtra("vless"):null; startAll(); }
        return START_STICKY;
    }

    private void startAll() {
        try {
            startForeground(1, buildNotif("Connecting..."));
            Builder b = new Builder().setSession("GOD OF VPN — V2Ray")
                .addAddress("10.10.0.2", 32).addDnsServer("1.1.1.1").addRoute("0.0.0.0", 0);
            tun = b.establish();

            File cfg = new File(getFilesDir(), "xray-config.json");
            write(cfg, VlessConfigBuilder.fromLink(vlessLink));

            File xrayBin = AssetsInstaller.ensureExec(this, "xray", "xray");
            File t2sBin  = AssetsInstaller.ensureExec(this, "tun2socks", "tun2socks");

            xrayProc = new ProcessBuilder(xrayBin.getAbsolutePath(), "-config", cfg.getAbsolutePath())
                .redirectErrorStream(true).start();

            t2sProc = new ProcessBuilder(
                t2sBin.getAbsolutePath(),
                "--netif-ipaddr","10.10.0.2",
                "--netif-netmask","255.255.255.0",
                "--socks-server-addr","127.0.0.1:10808",
                "--tunfd", String.valueOf(tun.getFd()),
                "--loglevel","notice"
            ).redirectErrorStream(true).start();

            RUNNING.set(true);
            thread = new Thread(this, "XrayTunnelLoop"); thread.start();
            updateNotif("Connected");
        } catch (Exception e) { stopAll(); }
    }

    @Override public void run() {
        try {
            if (xrayProc!=null) xrayProc.waitFor();
            if (t2sProc!=null) t2sProc.waitFor();
        } catch (InterruptedException ignored) {}
        stopAll();
    }

    private void stopAll() {
        RUNNING.set(false);
        if (xrayProc!=null) xrayProc.destroy();
        if (t2sProc!=null) t2sProc.destroy();
        if (thread!=null){ thread.interrupt(); thread=null; }
        if (tun!=null) try{ tun.close(); } catch (IOException ignored) {}
        stopForeground(true); stopSelf();
    }

    private static void write(File f, String s) throws IOException {
        try(FileOutputStream os=new FileOutputStream(f)){ os.write(s.getBytes()); }
    }

    private Notification buildNotif(String text){
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=26){
            nm.createNotificationChannel(new NotificationChannel(CH,"GOD OF VPN",NotificationManager.IMPORTANCE_LOW));
        }
        PendingIntent pi = PendingIntent.getActivity(this,0,new Intent(this, V2RayActivity.class),
            Build.VERSION.SDK_INT>=23? PendingIntent.FLAG_IMMUTABLE:0);
        return new NotificationCompat.Builder(this,CH)
            .setSmallIcon(android.R.drawable.stat_sys_vpn_ic)
            .setContentTitle("GOD OF VPN").setContentText(text)
            .setOngoing(true).setContentIntent(pi).build();
    }
    private void updateNotif(String t){ Notification n=buildNotif(t);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,n); }
}
