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

package com.chromium.fontinstaller.core;

import android.content.Context;
import android.content.SharedPreferences;

public class FontsterPreferences {

  private static final String PREFS_NAME = "com.chromium.fontinstaller.PREFS";

  public enum Key {
    ENABLE_TRUEFONT("1"),
    BACKUP_NAME("2"),
    BACKUP_DATE("3"),
    TRUEFONTS_CACHED("4"),
    DISABLE_PROMPT_TO_BACKUP("5");

    private final String id;

    Key(String id) { this.id = id; }
  }

  private final SharedPreferences mSharedPreferences;

  public FontsterPreferences(Context context) {
    mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public void putString(Key key, String value) {
    mSharedPreferences.edit().putString(key.id, value).apply();
  }

  public String getString(Key key) {
    return mSharedPreferences.getString(key.id, null);
  }

  public void putBoolean(Key key, boolean value) {
    mSharedPreferences.edit().putBoolean(key.id, value).apply();
  }

  public boolean getBoolean(Key key) {
    return mSharedPreferences.getBoolean(key.id, false);
  }
}
