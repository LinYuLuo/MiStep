package cn.xylin.mistep.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * @author XyLin
 * @date 2020年11月17日 23:01:00
 * WLOG.java
 **/
public class WLOG {
    private static File logFileDir;
    private static File logFilePath;

    public static void initLogFile(Context appContext) {
        try {
            logFileDir = MyFile.getFile(appContext).getDir("logs");
            Calendar calendar = Calendar.getInstance();
            logFilePath = new File(logFileDir, String.format("%s-%s-%s.log", calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH)));
        } catch (RuntimeException ignore) {
        }
    }

    public static void outLogs(String... logs) {
        if (logFileDir == null || logFilePath == null) {
            return;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(logFilePath, true);
            for (String log : logs) {
                outputStream.write(log.getBytes(StandardCharsets.UTF_8));
                outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException ignore) {
        }
    }

    public static void outThrowable(Throwable throwable) {
        for (StackTraceElement traceElement : throwable.getStackTrace()) {
            WLOG.outLogs(
                    "Class=" + traceElement.getClassName(),
                    "Method=" + traceElement.getMethodName(),
                    "Line=" + traceElement.getLineNumber(),
                    "StackTrace=" + traceElement.toString(), ""
            );
        }
        WLOG.outLogs(
                "Throwable=" + throwable.toString(), "",
                "Device Brand=" + Build.BRAND,
                "Device Model=" + Build.MODEL,
                "Android Device=" + Build.DEVICE,
                "Android ID=" + Build.DISPLAY,
                "Android Incremental=" + Build.VERSION.INCREMENTAL,
                "Android Sdk=" + Build.VERSION.SDK_INT,
                "Android Version=" + Build.VERSION.RELEASE,
                "--------------------------------------"
        );
    }

    public static void printLogs(String tag, Object... logs) {
        StringBuilder builder = new StringBuilder(tag).append("：");
        for (Object log : logs) {
            builder.append(log);
        }
        Log.i("WLOG", builder.toString());
    }
}
