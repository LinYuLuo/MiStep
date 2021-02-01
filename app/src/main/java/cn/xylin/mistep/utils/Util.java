package cn.xylin.mistep.utils;

import android.app.Activity;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author XyLin
 * @date 2020/5/3 09:48:01
 * Util.java
 **/
public class Util {
    public static final String STRING_NULL = "";
    public static final boolean BOOLEAN_NULL = false;
    public static final int INT_NULL = 0;
    public static final long DELAY_TIME = 2000L;
    //public static final ExecutorService TASK_SERVICE = Executors.newFixedThreadPool(5);

    public static boolean isStrEmpty(CharSequence sequence) {
        return TextUtils.isEmpty(sequence);
    }

    /**
     * @param equal    被拿来做相等判断主体的字符串
     * @param beEquals 被主体判断是否相等的字符串数组
     * @return 返回主体是否等于被判断的字符串数组中的一个
     */
    public static boolean isStrEquals(CharSequence equal, CharSequence... beEquals) {
        for (CharSequence beEqual : beEquals) {
            if (TextUtils.equals(equal, beEqual)) {
                return true;
            }
        }
        return BOOLEAN_NULL;
    }

    /**
     * @param contain    被拿来做包含判断主体的字符串
     * @param beContains 被主体判断是否包含的字符串数组
     * @return 返回主体是否包含被判断的字符串数组
     */
    public static boolean isStrContains(CharSequence contain, CharSequence... beContains) {
        for (CharSequence beContain : beContains) {
            if (contain.toString().contains(beContain)) {
                return true;
            }
        }
        return BOOLEAN_NULL;
    }

    public static String getControlText(Object control) {
        if (control instanceof EditText) {
            return ((EditText) control).getText().toString().trim();
        } else if (control instanceof Button) {
            return ((Button) control).getText().toString().trim();
        } else if (control instanceof TextView) {
            return ((TextView) control).getText().toString().trim();
        }
        return STRING_NULL;
    }

    public static void toast(Activity appActivity, Object idOrString) {
        toast(appActivity, idOrString, Toast.LENGTH_SHORT);
    }

    public static void toast(final Activity appActivity, final Object idOrString, final int duration) {
        if (appActivity != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                String str;
                if (idOrString instanceof Integer) {
                    str = appActivity.getString((int) idOrString);
                } else if (idOrString instanceof String) {
                    str = String.valueOf(idOrString);
                } else {
                    return;
                }
                Toast.makeText(appActivity.getApplicationContext(), str, duration).show();
            } else {
                appActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast(appActivity, idOrString, duration);
                    }
                });
            }
        }
    }
}
