package com.godvpn.nativehello;

import android.content.Context;
import android.os.Build;
import java.io.*;

public class AssetsInstaller {
    private static String abiFolder(){
        for (String abi : Build.SUPPORTED_ABIS) {
            if ("arm64-v8a".equalsIgnoreCase(abi)) return "arm64-v8a";
            if ("armeabi-v7a".equalsIgnoreCase(abi)) return "armeabi-v7a";
        }
        return "arm64-v8a";
    }
    public static File ensureExec(Context c, String baseDir, String binName) throws IOException {
        String assetPath = baseDir + "/" + abiFolder() + "/" + binName;
        File outDir = new File(c.getFilesDir(), baseDir);
        if (!outDir.exists()) outDir.mkdirs();
        File out = new File(outDir, binName);
        if (!out.exists()) {
            try (InputStream is = c.getAssets().open(assetPath);
                 FileOutputStream os = new FileOutputStream(out)) {
                byte[] buf = new byte[8192]; int n;
                while ((n = is.read(buf)) != -1) os.write(buf, 0, n);
            }
        }
        out.setExecutable(true);
        return out;
    }
}
