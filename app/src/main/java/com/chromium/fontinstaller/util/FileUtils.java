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

import android.app.ActivityManager;
import android.content.Context;

import com.koushikdutta.ion.Ion;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class FileUtils {

    public static void clearIonCache(Context context) {
        Ion.getDefault(context).configure().getResponseCache().clear();
    }

    public static int getMaxCacheSize(Context context) {
        int memClass = ((ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        return 1024 * 1024 * memClass / 8;
    }
}
