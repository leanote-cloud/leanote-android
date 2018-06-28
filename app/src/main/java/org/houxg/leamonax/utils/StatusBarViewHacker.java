package org.houxg.leamonax.utils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.houxg.leamonax.R;

import static org.houxg.leamonax.ui.Navigation.SP_THEME_NIGHT;
import static org.houxg.leamonax.utils.StatusBarUtils.FAKE_STATUS_BAR_VIEW_ID;

public class StatusBarViewHacker {
    public static void fixStatusBarBackgroundColor(Activity activity) {
        View fakeStatusBarView = activity.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            int id;
            boolean result = SharedPreferenceUtils.read(SharedPreferenceUtils.LEANOTE, SP_THEME_NIGHT, false);
            if (result) {
                id = R.color.colorPrimary_night;
            } else {
                id = R.color.colorPrimary;
            }
            fakeStatusBarView.setBackgroundColor(ContextCompat.getColor(activity, id));
        }
    }
}
