package cn.xylin.mistep.activitys;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.TimeZone;

import cn.xylin.mistep.R;
import cn.xylin.mistep.StepApplication;
import cn.xylin.mistep.utils.Shared;
import cn.xylin.mistep.utils.StepUtil;
import cn.xylin.mistep.utils.Util;
import cn.xylin.mistep.works.AutoModifySteps;

/**
 * @author XyLin
 * @date 2021/1/25 23:06:22
 * Main.java
 **/
public class Main extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {
    public static final String USER_SETTING = "userSetting";
    public static final String ROOT_MODE = "isRootMode";
    public static final String MODIFY_MODE_ADD = "modifyModeAdd";
    public static final String AUTO_MODIFY_MODE = "autoModifyMode";
    public static final String AUTO_ADD_STEPS = "autoAddSteps";
    private static final String DAY_INT = "dayInt";
    private MaterialTextView tvTodaySteps;
    private TextInputEditText edtAddSteps;
    private RadioGroup rdgModifyMode;
    private SwitchMaterial shFirstOpenAutoAdd, shTimingModify;
    private Shared shared;
    private int autoModifyMode;
    private boolean isRootMode;

    @Override
    void initActivityControl() {
        isCanExitActivity = true;
        setContentView(R.layout.activity_main);
        tvTodaySteps = findViewById(R.id.tvTodaySteps);
        edtAddSteps = findViewById(R.id.edtAddSteps);
        rdgModifyMode = findViewById(R.id.rdgModifyMode);
        shFirstOpenAutoAdd = findViewById(R.id.shFirstOpenAutoAdd);
        shTimingModify = findViewById(R.id.shTimingModify);
        shared = Shared.getShared();
    }

    @Override
    void initControlAttribute() {
        isRootMode = shared.getValue(USER_SETTING, ROOT_MODE, false);
        updateTodaySteps();
        edtAddSteps.setText(shared.getValue(USER_SETTING, AUTO_ADD_STEPS, 0).toString());
        rdgModifyMode.check(shared.getValue(USER_SETTING, MODIFY_MODE_ADD, true) ? R.id.rdbAddSteps : R.id.rdbSetSteps);
        autoModifyMode = shared.getValue(USER_SETTING, AUTO_MODIFY_MODE, 0);
        shFirstOpenAutoAdd.setChecked(autoModifyMode == 1);
        shTimingModify.setChecked(autoModifyMode == 2);
        rdgModifyMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                shared.putValue(USER_SETTING, MODIFY_MODE_ADD, checkedId == R.id.rdbAddSteps).commitShared(USER_SETTING);
            }
        });
        shFirstOpenAutoAdd.setOnCheckedChangeListener(this);
        shTimingModify.setOnCheckedChangeListener(this);
        MaterialButton button = findViewById(R.id.btnModifySteps);
        button.setOnClickListener(this);
        button.setOnLongClickListener(this);
        ((MaterialTextView) findViewById(R.id.tvUseTip)).setMovementMethod(ScrollingMovementMethod.getInstance());
        if (autoModifyMode == 1) {
            checkNewDay();
        }
    }

    private void checkNewDay() {
        int day = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH);
        if (shared.getValue(USER_SETTING, DAY_INT, -1) != day) {
            shared.putValue(USER_SETTING, DAY_INT, day).commitShared(USER_SETTING);
            findViewById(R.id.btnModifySteps).callOnClick();
            StepApplication.finishAll();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnModifySteps) {
            String inputStr = Util.getControlText(edtAddSteps);
            if (Util.isStrEmpty(inputStr)) {
                Util.toast(appActivity, R.string.toast_empty_steps);
            } else {
                try {
                    int steps = Integer.parseInt(inputStr);
                    shared.putValue(USER_SETTING, AUTO_ADD_STEPS, steps).commitShared(USER_SETTING);
                    Util.toast(appActivity, StepUtil.modifySteps(getApplicationContext(), rdgModifyMode.getCheckedRadioButtonId() == R.id.rdbSetSteps, isRootMode, steps) ? "修改步数成功。" : "修改步数失败，请尝试切换模式。");
                    updateTodaySteps();
                } catch (NumberFormatException ignore) {
                    Util.toast(appActivity, R.string.toast_input_steps_error);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int tempMode = autoModifyMode;
        if (isChecked) {
            autoModifyMode = buttonView.getId() == R.id.shFirstOpenAutoAdd ? 1 : 2;
            shFirstOpenAutoAdd.setChecked(autoModifyMode == 1);
            shTimingModify.setChecked(autoModifyMode == 2);
        } else if (!shFirstOpenAutoAdd.isChecked() && !shTimingModify.isChecked()) {
            autoModifyMode = 0;
        }
        if (autoModifyMode != tempMode) {
            AutoModifySteps.setNextModifySteps(getApplicationContext(), autoModifyMode != 2);
            shared.putValue(USER_SETTING, AUTO_MODIFY_MODE, autoModifyMode).commitShared(USER_SETTING);
        }
    }

    private void updateTodaySteps() {
        tvTodaySteps.setText("今日步数：" + StepUtil.getTodaySteps(getApplicationContext()));
    }

    @Override
    public boolean onLongClick(View v) {
        isRootMode = !isRootMode;
        Util.toast(appActivity, "已切换为" + (isRootMode ? "ROOT" : "核心破解") + "模式。");
        shared.putValue(USER_SETTING, ROOT_MODE, isRootMode).commitShared(USER_SETTING);
        return true;
    }
}
