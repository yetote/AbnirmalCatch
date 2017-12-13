package com.example.admin.abnirmalcatch;

import android.app.Application;

/**
 * com.example.admin.abnirmalcatch
 *
 * @author Swg
 * @date 2017/12/13 16:10
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
