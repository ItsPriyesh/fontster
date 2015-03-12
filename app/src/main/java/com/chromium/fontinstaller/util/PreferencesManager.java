/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chromium.fontinstaller.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by priyeshpatel on 15-03-11.
 */
public class PreferencesManager {
    private static final String PREFS_NAME = "com.chromium.fontinstaller.PREFS";

    public static final String KEY_ENABLE_TRUEFONT = "1";
    public static final String KEY_BACKUP_NAME = "2";
    public static final String KEY_BACKUP_DATE = "3";

    private static SharedPreferences sharedPreferences = null;
    private static PreferencesManager prefsManager = null;

    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (prefsManager == null) {
            prefsManager = new PreferencesManager(context);
        }
        return prefsManager;
    }

    public void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

}
