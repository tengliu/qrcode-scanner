package com.sduwh.qrcodeSacnner;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by chundk on 2017-01-07.
 */
public class ScreenUtil {

    /**
     * 获得 densityDpi
     *
     * @param activity
     * @return
     */
    public static int getDPI(Activity ctx){
        return getDPI(ctx.getWindowManager());
    }

    public static int getDPI(WindowManager wm){
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.densityDpi;
    }

    /**
     * 获得屏幕尺寸
     *
     */
    public static int[] getScreenSize(Activity ctx) {
        return getScreenSize(ctx.getWindowManager());
    }

    public static int[] getScreenSize(WindowManager mWindowManager) {
        switch(Build.VERSION.SDK_INT){

            default:
            case Build.VERSION_CODES.LOLLIPOP_MR1:
            case Build.VERSION_CODES.LOLLIPOP:
                int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
                int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
                return new int[] { screenWidth, screenHeight };

            case Build.VERSION_CODES.M:
                Point size = new Point();
                mWindowManager.getDefaultDisplay().getSize(size);
                int width = size.x;
                int height = size.y;
                return new int[] { width, height };
        }
    }
}
