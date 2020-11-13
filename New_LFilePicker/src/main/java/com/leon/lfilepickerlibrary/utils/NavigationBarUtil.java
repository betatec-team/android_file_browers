package com.leon.lfilepickerlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Description：控制虚拟栏
 * Created by leon on 2018/11/28.
 */
public class NavigationBarUtil {
    //static int uiOptions;

    /**
     * 隐藏虚拟栏 ，显示的时候再隐藏掉
     *
     * @param window
     */
    static public void hideNavigationBar(final Window window) {
        if (isShowNavigationBar(window))
            hideSystemUI(window);
//        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                hideSystemUI(window);
//            }
//        });
    }

    static public void showNavigationBar(final Window window) {
        if (!isShowNavigationBar(window))
            showSystemUI(window);
//        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                showSystemUI(window);
//            }
//        });
    }

    static public boolean isShowNavigationBar(final Window window) {
        return ((window.getDecorView().getSystemUiVisibility() & 0x00002000) > 0) ? false : true;

    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    static public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    static public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    // This snippet hides the system bars.
    static public void hideSystemUI(Window window) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | 0x00002000);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    static public void showSystemUI(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    static public boolean mt8735GetNavigationBarState(Context context) {
        int status = Settings.System.getInt(context.getContentResolver(), "navigation_bar_switch", 0);
        return status != 0;
    }

    static public void mt8735ShowNavigationBar(Context context) {
        String action = "android.intent.action.NAVIGATION_BAR";
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("enable", true);
        context.sendBroadcast(intent);
    }

    static public void mt8735HideNavigationBar(Context context) {
        String action = "android.intent.action.NAVIGATION_BAR";
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("enable", false);
        context.sendBroadcast(intent);
    }
}