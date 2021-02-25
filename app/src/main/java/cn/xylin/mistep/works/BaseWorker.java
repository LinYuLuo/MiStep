package cn.xylin.mistep.works;

import android.content.Context;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import cn.xylin.mistep.activitys.Main;
import cn.xylin.mistep.utils.Shared;

/**
 * @author XyLin
 * @date 2021/1/29 16:17:14
 * BaseWorker.java
 **/
abstract class BaseWorker extends Worker {
    /**
     * 获取下一次执行定时任务需要等待的时间
     *
     * @return 返回执行下一次任务需要等待的毫秒数
     */
    static long getNextTime() {
        Calendar currCalendar = Calendar.getInstance();
        Calendar nextCalendar = Calendar.getInstance();
        Shared shared = Shared.getShared();
        nextCalendar.set(Calendar.HOUR_OF_DAY, shared.getValue(Main.USER_SETTING, Main.TIMING_HOUR, 0));
        nextCalendar.set(Calendar.MINUTE, shared.getValue(Main.USER_SETTING, Main.TIMING_MINUTE, 1));
        nextCalendar.set(Calendar.SECOND, 0);
        if (nextCalendar.before(currCalendar)) {
            nextCalendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        return nextCalendar.getTimeInMillis() - currCalendar.getTimeInMillis();
    }

    BaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
}
