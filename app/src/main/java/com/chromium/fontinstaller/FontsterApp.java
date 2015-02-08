package com.chromium.fontinstaller;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by priyeshpatel on 15-02-06.
 */
public class FontsterApp extends Application {
    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
