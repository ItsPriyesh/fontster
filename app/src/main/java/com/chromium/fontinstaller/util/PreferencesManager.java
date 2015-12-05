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

public class PreferencesManager {

  private static final String PREFS_NAME = "com.chromium.fontinstaller.PREFS";

  public static final class Keys {
    public static final String ENABLE_TRUEFONT = "1";
    public static final String BACKUP_NAME = "2";
    public static final String BACKUP_DATE = "3";
    public static final String TRUEFONTS_CACHED = "4";
  }

  private static SharedPreferences sSharedPreferences = null;
  private static PreferencesManager sPreferencesManager = null;

  private PreferencesManager(Context context) {
    sSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static synchronized PreferencesManager getInstance(Context context) {
    if (sPreferencesManager == null) {
      sPreferencesManager = new PreferencesManager(context);
    }
    return sPreferencesManager;
  }

  public void setString(String key, String value) {
    sSharedPreferences.edit().putString(key, value).apply();
  }

  public String getString(String key) {
    return sSharedPreferences.getString(key, null);
  }

  public void setBoolean(String key, boolean value) {
    sSharedPreferences.edit().putBoolean(key, value).apply();
  }

  public boolean getBoolean(String key) {
    return sSharedPreferences.getBoolean(key, false);
  }
}
