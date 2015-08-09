package com.stpi.campus.activity.otherService;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lyl on 2014/9/10.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    private Context defaultContext;

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        defaultContext = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d("thread name:" + thread.getName(), "thread id" + thread.getId(), ex);
        handleException(ex);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private boolean handleException(Throwable ex) {
        if (ex == null)
            return false;
        final String log = getErrorLog(ex);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(defaultContext, log, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        saveLog2File(log);
        return true;
    }

    public String getErrorLog(Throwable ex) {
        String log;
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        log = printWriter.toString() + ex.getMessage();
        return log;
    }

    public void saveLog2File(String log) {
        try {
            Long timestamp = System.currentTimeMillis();
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd-HH-mm-ss");
            String date = format.format(new Date());
            String fileName = "crash-" + date + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(log.getBytes());
                fos.close();
            }
            return;
        } catch (Exception e) {
            Log.e("Error", "an error occured while writing file...", e);
        }
        return;
    }
}
