package me.daei.soundmeter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by su on 2016/6/6.
 */
public class ScreenUtil {
    private ScreenUtil() {
    }

    /**
     * 屏幕密度
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        if (context instanceof Activity) {
            context = context.getApplicationContext();
        }
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.density;
    }
}
