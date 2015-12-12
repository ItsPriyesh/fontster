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

package com.chromium.fontinstaller;

import android.app.Application;
import android.content.Context;

import com.chromium.fontinstaller.ui.settings.SettingsFragment;
import com.crashlytics.android.Crashlytics;

import javax.inject.Singleton;

import dagger.Component;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class FontsterApp extends Application {

  @Singleton
  @Component(modules = {FontsterModule.class})
  public interface FontsterComponent {
    void inject(SettingsFragment settingsFragment);
    void inject(FontsterApp fontsterApp);
  }

  private FontsterComponent mComponent;

  @Override public void onCreate() {
    super.onCreate();

   // mComponent = DaggerFontsterApp_FontsterComponent.builder()
     //   .fontsterModule(new FontsterModule(this))
       // .build();

    Fabric.with(this, new Crashlytics());

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  public static FontsterComponent get(Context context) {
    return ((FontsterApp) context.getApplicationContext()).mComponent;
  }

}