package cn.xylin.mistep;

import android.app.Activity;
import android.app.Application;
import java.util.ArrayList;
import cn.xylin.mistep.activitys.Main;
import cn.xylin.mistep.utils.MyCrash;
import cn.xylin.mistep.utils.Shared;
import cn.xylin.mistep.utils.WLOG;


/**
 * @author XyLin
 * @date 2020年11月17日 23:01:00
 * StepApplication.java
 **/
public class StepApplication extends Application {
    private static final ArrayList<Activity> ACTIVITYS = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        WLOG.initLogFile(getApplicationContext());
        MyCrash.getInstance().initContext(getApplicationContext());
        Shared.getShared().addShared(getApplicationContext(), Main.USER_SETTING);
    }

    public static void add(Activity appActivity) {
        ACTIVITYS.add(appActivity);
    }

    public static void remove(Activity appActivity) {
        ACTIVITYS.remove(appActivity);
    }

    public static Activity get() {
        return ACTIVITYS.get(ACTIVITYS.size() - 1);
    }

    public static void finishAll() {
        int size = ACTIVITYS.size();
        for (int index = 0; index < size; index++) {
            if ((index + 1) < size) {
                ACTIVITYS.get(index).finish();
            } else {
                ACTIVITYS.get(index).finishAndRemoveTask();
            }
        }
        ACTIVITYS.clear();
        //Util.TASK_SERVICE.shutdownNow();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
