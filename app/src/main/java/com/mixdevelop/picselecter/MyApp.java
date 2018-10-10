package com.mixdevelop.picselecter;

import android.app.Application;

import cn.com.mark.multiimage.core.PreferencesUtils;

/**
 * Author:wang_sir
 * Time:2018/10/9 10:53
 * Description:This is MyApp
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesUtils.init(this, "multiimage");

    }
}
