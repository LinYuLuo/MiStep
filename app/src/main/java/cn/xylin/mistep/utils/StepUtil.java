package cn.xylin.mistep.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author XyLin
 * @date 2021/1/26 17:28:20
 * StepUtil.java
 **/
public class StepUtil {
    private static final String[] MAY_BE_RECORD_APP = {"com.miui.powerkeeper", "com.xiaomi.joyose"};
    private static final Uri STEP_URI = Uri.parse("content://com.miui.providers.steps/item");
    private static final String ROOT_ADD_STEPS = "content insert --uri content://com.miui.providers.steps/item --bind _begin_time:l:%d --bind _end_time:l:%d --bind _mode:i:2 --bind _steps:i:%d";
    private static final String BEGIN_TIME = "_begin_time";
    private static final String END_TIME = "_end_time";
    private static final String MODE = "_mode";
    private static final String STEPS = "_steps";
    private static final String[] QUERY_FILED = {"_id", BEGIN_TIME, END_TIME, MODE, STEPS};
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
    private static final ContentValues STEPS_VALUE = new ContentValues(4);

    static {
        STEPS_VALUE.put(MODE, 2);
    }

    public static int getTodaySteps(Context context) {
        try {
            Cursor cursor = context.getContentResolver().query(STEP_URI, QUERY_FILED, null, null, null);
            if (cursor != null) {
                long todayBeginTime = TIME_FORMAT.parse(getTodayTime(true)).getTime();
                long todayEndTime = TIME_FORMAT.parse(getTodayTime(false)).getTime();
                int todayStepCount = 0;
                while (cursor.moveToNext()) {
                    if (cursor.getLong(1) > todayBeginTime && cursor.getLong(2) < todayEndTime && cursor.getInt(3) == 2) {
                        todayStepCount += cursor.getInt(4);
                    }
                }
                cursor.close();
                return todayStepCount;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private static String getTodayTime(boolean flag) {
        return String.format("%s%s", TIME_FORMAT.format(System.currentTimeMillis()).substring(0, 11), flag ? "00:00:00" : "23:59:59");
    }

    public static boolean modifySteps(Context context, boolean isSetMode, boolean isRootMode, int steps) {
        int todaySteps = getTodaySteps(context);
        if (isSetMode) {
            if (todaySteps == steps) {
                return true;
            } else {
                steps -= todaySteps;
            }
        }
        if (isRootMode) {
            return rootModeAddSteps(steps);
        } else {
            return coreModeAddSteps(context, steps);
        }
    }

    private static boolean coreModeAddSteps(Context context, int steps) {
        try {
            STEPS_VALUE.put(BEGIN_TIME, System.currentTimeMillis() - 1000L);
            STEPS_VALUE.put(END_TIME, System.currentTimeMillis());
            STEPS_VALUE.put(STEPS, steps);
            context.getContentResolver().insert(STEP_URI, STEPS_VALUE);
            return true;
        } catch (SecurityException ignore) {
        }
        return false;
    }

    private static boolean runRootCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(command);
            dataOutputStream.flush();
            dataOutputStream.close();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException ignored) {
        }
        return false;
    }

    private static boolean rootModeAddSteps(int steps) {
        return runRootCommand(String.format(
                Locale.CHINA,
                ROOT_ADD_STEPS,
                System.currentTimeMillis() - 1000L,
                System.currentTimeMillis(),
                steps
        ));
    }

    public static boolean rootEnableRecordApp() {
        boolean enableSuccess = false;
        for (String pkg : MAY_BE_RECORD_APP) {
            boolean enableResult = runRootCommand("pm enable " + pkg);
            if (!enableSuccess && enableResult) {
                enableSuccess = true;
            }
        }
        return enableSuccess;
    }

    public static boolean checkRecordDisable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            for (String pkg : MAY_BE_RECORD_APP) {
                ApplicationInfo info = packageManager.getApplicationInfo(pkg, 0);
                if (!info.enabled) {
                    return false;
                }
            }
            return true;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
    }
}
