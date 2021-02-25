package cn.xylin.mistep.activitys;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
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
    public static final String TIMING_NOTIFICATION = "isNotificationTimingModifyResult";
    public static final String TIMING_HOUR = "timingHour";
    public static final String TIMING_MINUTE = "timingMinute";
    private static final String DAY_INT = "dayInt";
    private static final String CHECK_RECORD_APP = "checkRecordApp";
    private MaterialTextView tvTodaySteps;
    private TextInputEditText edtAddSteps;
    private RadioGroup rdgModifyMode;
    private SwitchMaterial shFirstOpenAutoAdd, shTimingModify, shTimingNotification;
    private Shared shared;
    private boolean isRootMode;
    private TimePickerDialog timeDialog;

    @Override
    void initActivityControl() {
        isCanExitActivity = true;
        setContentView(R.layout.activity_main);
        tvTodaySteps = findViewById(R.id.tvTodaySteps);
        edtAddSteps = findViewById(R.id.edtAddSteps);
        rdgModifyMode = findViewById(R.id.rdgModifyMode);
        shFirstOpenAutoAdd = findViewById(R.id.shFirstOpenAutoAdd);
        shTimingModify = findViewById(R.id.shTimingModify);
        shTimingNotification = findViewById(R.id.shTimingNotification);
        shared = Shared.getShared();
    }

    @Override
    void initControlAttribute() {
        isRootMode = shared.getValue(USER_SETTING, ROOT_MODE, false);
        updateTodaySteps();
        edtAddSteps.setText(shared.getValue(USER_SETTING, AUTO_ADD_STEPS, 0).toString());
        rdgModifyMode.check(shared.getValue(USER_SETTING, MODIFY_MODE_ADD, true) ? R.id.rdbAddSteps : R.id.rdbSetSteps);
        int autoModifyMode = shared.getValue(USER_SETTING, AUTO_MODIFY_MODE, 0);
        shFirstOpenAutoAdd.setChecked(autoModifyMode == 1);
        shTimingModify.setChecked(autoModifyMode == 2);
        if (autoModifyMode == 2) {
            shTimingNotification.setVisibility(View.VISIBLE);
            shTimingNotification.setChecked(shared.getValue(USER_SETTING, TIMING_NOTIFICATION, false));
        }
        rdgModifyMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                shared.putValue(USER_SETTING, MODIFY_MODE_ADD, checkedId == R.id.rdbAddSteps).commitShared(USER_SETTING);
            }
        });
        shFirstOpenAutoAdd.setOnCheckedChangeListener(this);
        shTimingModify.setOnCheckedChangeListener(this);
        shTimingNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                shared.putValue(USER_SETTING, TIMING_NOTIFICATION, isChecked).commitShared(USER_SETTING);
            }
        });
        MaterialButton button = findViewById(R.id.btnModifySteps);
        button.setOnClickListener(this);
        button.setOnLongClickListener(this);
        ((MaterialTextView) findViewById(R.id.tvUseTip)).setMovementMethod(ScrollingMovementMethod.getInstance());
        if (autoModifyMode == 1) {
            checkNewDay();
            return;
        }
        if (!shared.getValue(USER_SETTING, CHECK_RECORD_APP, false)) {
            checkRecordAppState();
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
        int switchId = buttonView.getId();
        if (buttonView.isPressed()) {
            switch (switchId) {
                case R.id.shFirstOpenAutoAdd: {
                    if (isChecked) {
                        shTimingModify.setChecked(false);
                        shared.putValue(USER_SETTING, DAY_INT, Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH));
                    } else {
                        shared.removeValue(USER_SETTING, DAY_INT);
                    }
                    break;
                }
                case R.id.shTimingModify: {
                    if (isChecked) {
                        if (timeDialog == null) {
                            timeDialog = new TimePickerDialog(
                                    appActivity,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            shFirstOpenAutoAdd.setChecked(false);
                                            shTimingNotification.setVisibility(View.VISIBLE);
                                            shared.putValue(USER_SETTING, TIMING_HOUR, hourOfDay)
                                                    .putValue(USER_SETTING, TIMING_MINUTE, minute)
                                                    .commitShared(USER_SETTING);
                                            AutoModifySteps.setNextModifySteps(getApplicationContext(), false);
                                            Util.toast(appActivity, "将于每天的" + hourOfDay + "点" + (minute > 0 && minute < 10 ? "0" + minute : minute) + "分自动修改");
                                        }
                                    },
                                    shared.getValue(USER_SETTING, TIMING_HOUR, 0),
                                    shared.getValue(USER_SETTING, TIMING_MINUTE, 1),
                                    true
                            );
                            timeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    shTimingModify.setChecked(false);
                                    AutoModifySteps.setNextModifySteps(getApplicationContext(), true);
                                }
                            });
                        }
                        timeDialog.show();
                        Util.toast(appActivity, R.string.toast_please_appoint_time);
                    } else {
                        shTimingNotification.setVisibility(View.GONE);
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            shared.putValue(USER_SETTING, AUTO_MODIFY_MODE, !shFirstOpenAutoAdd.isChecked() && !shTimingModify.isChecked() ? 0 : shFirstOpenAutoAdd.isChecked() ? 1 : 2)
                    .commitShared(USER_SETTING);
        } else if (!isChecked) {
            if (switchId == R.id.shFirstOpenAutoAdd) {
                shared.removeValue(USER_SETTING, DAY_INT).commitShared(USER_SETTING);
            } else if (switchId == R.id.shTimingModify) {
                shTimingNotification.setVisibility(View.GONE);
                AutoModifySteps.setNextModifySteps(getApplicationContext(), true);
            }
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

    private void checkRecordAppState() {
        if (!StepUtil.checkRecordDisable(getApplicationContext())) {
            new AlertDialog.Builder(appActivity)
                    .setTitle(R.string.dialog_record_title)
                    .setMessage(R.string.dialog_record_message)
                    .setNeutralButton(R.string.dialog_record_btn_enable, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Util.toast(appActivity, StepUtil.rootEnableRecordApp() ? R.string.toast_record_enable_success : R.string.toast_record_enable_fail);
                            updateTodaySteps();
                        }
                    })
                    .setNegativeButton(R.string.dialog_record_btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shared.putValue(USER_SETTING, CHECK_RECORD_APP, true).applyShared(USER_SETTING);
                        }
                    })
                    .setPositiveButton(R.string.dialog_record_btn_ok, null)
                    .show();
        }
    }
}
