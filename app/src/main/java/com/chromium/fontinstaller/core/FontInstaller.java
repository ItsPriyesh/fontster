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

import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class FontInstaller {
    private FontPackage fontPackage;
    private Context context;
    private List<String> copyCommands = new ArrayList<>();

    public static final String FONT_INSTALL_DIR = "/system/fonts";
    public static final String CACHE_DIR = "/sdcard/Android/data/com.chromium.fontinstaller/cache/";

    public FontInstaller(FontPackage fontPackage, Context context) {
        this.fontPackage = fontPackage;
        this.context = context;

    }

    public void install() {
        if (Shell.SU.available()) {
            for (Font font : fontPackage.getFontList()) {
                copyCommands.add("cp " + CACHE_DIR + fontPackage.getNameFormatted() +
                        File.separator + font.getName() + " " + FONT_INSTALL_DIR);
            }

            InstallTask installTask = new InstallTask(context);
            installTask.execute(copyCommands.toArray(new String[copyCommands.size()]));
        }
    }
}
