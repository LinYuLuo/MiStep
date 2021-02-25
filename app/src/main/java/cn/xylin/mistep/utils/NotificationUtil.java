package cn.xylin.mistep.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import cn.xylin.mistep.BuildConfig;
import cn.xylin.mistep.R;

/**
 * @author XyLin
 * @date 2021/2/24 21:42:09
 * NotificationUtil.java
 **/
public class NotificationUtil {
    private static NotificationUtil instance;
    private static final String CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private static final boolean NEED_CHANNEL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static NotificationUtil get() {
        if (instance == null) {
            instance = new NotificationUtil();
        }
        return instance;
    }

    private Context context;
    private final NotificationChannel channel;
    private NotificationManager notificationManager;
    //该项目去掉通知ID，已碰到指定步数多次发送成功通知的问题，但Debug却没有触发
    //private int notificationId = 0;

    private NotificationUtil() {
        if (NEED_CHANNEL) {
            channel = new NotificationChannel(CHANNEL_ID, "定时修改结果通知", NotificationManager.IMPORTANCE_LOW);
        } else {
            channel = null;
        }
    }

    public void init(Context appContext) {
        context = appContext;
        notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (NEED_CHANNEL) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title, String message) {
        sendNotification(R.drawable.ic_app, title, message);
    }

    public void sendNotification(@DrawableRes int icon, String title, String message) {
        if (context != null) {
            //notificationId++;
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .build();
            notificationManager.notify(0, notification);
        }
    }

    public void cancelNotification(int notificationId) {
        if (context != null) {
            notificationManager.cancel(notificationId);
        }
    }
}
