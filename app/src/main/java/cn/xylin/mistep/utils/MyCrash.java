package cn.xylin.mistep.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import cn.xylin.mistep.StepApplication;

/**
 * @author XyLin
 * @date 2020年3月14日 09:53:13
 * MyCrash.java 用于捕捉全局异常
 **/
public class MyCrash implements Thread.UncaughtExceptionHandler {
    private static final Object SYN_ROOT = new Object();
    private Context myContext;
    private Thread.UncaughtExceptionHandler defaultHandler;

    public static MyCrash getInstance() {
        synchronized (SYN_ROOT) {
            return new MyCrash();
        }
    }

    public void initContext(Context myContext) {
        this.myContext = myContext;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!isHandlerSuccess(throwable) && defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException ignored) {
            }
            StepApplication.finishAll();
        }
    }

    private boolean isHandlerSuccess(final Throwable throwable) {
        if (throwable == null) {
            return true;
        }
        if (throwable.toString().trim().length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    WLOG.outThrowable(throwable);
                    Toast.makeText(myContext, "程序出现未知错误，请查看日志文件。", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
            return true;
        }
        return false;
    }
}
