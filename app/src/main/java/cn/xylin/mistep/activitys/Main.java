package cn.xylin.mistep.activitys;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import cn.xylin.mistep.R;

/**
 * @author XyLin
 * @date 2021/1/25 23:06:22
 * Main.java
 **/
public class Main extends BaseActivity {
    @Override
    void initActivityControl() {
        setContentView(R.layout.activity_main);
    }

    @Override
    void initControlAttribute() {
        ((TextView)findViewById(R.id.tvUseTip)).setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
