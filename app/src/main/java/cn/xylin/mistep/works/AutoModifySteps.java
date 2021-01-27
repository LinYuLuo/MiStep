package cn.xylin.mistep.works;

import android.content.Context;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import cn.xylin.mistep.activitys.Main;
import cn.xylin.mistep.utils.Shared;
import cn.xylin.mistep.utils.StepUtil;

/**
 * @author XyLin
 * @date 2021/1/27 19:18:55
 * AutoModifySteps.java
 **/
public class AutoModifySteps extends Worker {
    private static final String WORK_TAG = "autoModifySteps";

    /**
     * 获取下一次执行定时任务需要等待的时间
     *
     * @return 返回执行下一次任务需要等待的毫秒数
     */
    private static long getNextTime() {
        Calendar currCalendar = Calendar.getInstance();
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.set(Calendar.HOUR_OF_DAY, 0);
        nextCalendar.set(Calendar.MINUTE, 1);
        nextCalendar.set(Calendar.SECOND, 0);
        if (nextCalendar.before(currCalendar)) {
            nextCalendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        return nextCalendar.getTimeInMillis() - currCalendar.getTimeInMillis();
    }

    /**
     * 用来开启或关闭定时增加步数功能
     *
     * @param context
     * @param isCancel 指定是否关闭定时增加步数，只可通过Main界面进行调整
     */
    public static void setNextModifySteps(Context context, boolean isCancel) {
        WorkManager manager = WorkManager.getInstance(context);
        if (isCancel) {
            manager.cancelAllWorkByTag(WORK_TAG);
        } else {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AutoModifySteps.class)
                    .addTag(WORK_TAG)
                    .setInitialDelay(getNextTime(), TimeUnit.MILLISECONDS)
                    .build();
            manager.enqueue(workRequest);
        }
    }

    public AutoModifySteps(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        setNextModifySteps(getApplicationContext(), false);
        StepUtil.modifySteps(
                getApplicationContext(),
                !Shared.getShared().getValue(Main.XML_SETTING, Main.MODIFY_MODE_ADD, true),
                Shared.getShared().getValue(Main.XML_SETTING, Main.ROOT_MODE, false),
                Shared.getShared().getValue(Main.XML_SETTING, Main.AUTO_ADD_STEPS, 0)
        );
        return Worker.Result.success();
    }
}
