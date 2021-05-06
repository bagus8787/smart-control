package com.example.smart_control.services;

import android.os.Handler;

import java.io.IOException;

public class CekConnectionService {

    public boolean cekConnection() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                    try {
                        Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                        int     exitValue = ipProcess.waitFor();
//                        return (exitValue == 0);
                    }
                    catch (IOException e)          { e.printStackTrace(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();

        return false;
    }
}
