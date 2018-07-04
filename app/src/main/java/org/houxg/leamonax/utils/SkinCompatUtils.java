package org.houxg.leamonax.utils;

import static org.houxg.leamonax.ui.Navigation.SP_THEME_NIGHT;

public class SkinCompatUtils {
    public static boolean isThemeNight() {
        return SharedPreferenceUtils.read(SharedPreferenceUtils.LEANOTE, SP_THEME_NIGHT, false);
    }
}
