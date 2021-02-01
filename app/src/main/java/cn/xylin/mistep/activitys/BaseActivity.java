package cn.xylin.mistep.activitys;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import cn.xylin.mistep.R;
import cn.xylin.mistep.StepApplication;
import cn.xylin.mistep.utils.Util;

/**
 * @author XyLin
 * @date 2020年11月21日 22:31:00
 * BaseActivity.java
 **/
public abstract class BaseActivity extends AppCompatActivity {

    AppCompatActivity appActivity;
    boolean isCanExitActivity = false;
    long clickBackTime;
    MaterialToolbar trBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.content_view);
        appActivity = this;
        StepApplication.add(appActivity);
        trBar = findViewById(R.id.trBar);
        setSupportActionBar(trBar);
        initActivityControl();
        initControlAttribute();
    }

    abstract void initActivityControl();

    abstract void initControlAttribute();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StepApplication.remove(appActivity);
    }

    @Override
    public void onBackPressed() {
        if (isCanExitActivity) {
            if ((System.currentTimeMillis() - clickBackTime) > Util.DELAY_TIME) {
                clickBackTime = System.currentTimeMillis();
                Util.toast(appActivity, "再按一次以退出程序");
                return;
            }
            StepApplication.finishAll();
        }
        super.onBackPressed();
    }

    @Override
    public void setContentView(@LayoutRes int layoutId) {
        ((LinearLayout) findViewById(R.id.llGroup)).addView(getLayoutInflater().inflate(layoutId, null));
    }
}
