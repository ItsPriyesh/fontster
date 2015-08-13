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
import com.chromium.fontinstaller.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FontInstaller {
    private FontPackage fontPackage;
    private Context context;
    private List<String> copyCommands = new ArrayList<>();

    private static final String MOUNT_SYSTEM = "mount -o rw,remount /system";
    private static final String FONT_INSTALL_DIR = "/system/fonts";
    private String cacheDir;

    public FontInstaller(FontPackage fontPackage, Context context) {
        this.fontPackage = fontPackage;
        this.context = context;

        cacheDir = context.getExternalCacheDir() + File.separator;
        for (Font font : fontPackage.getFontList()) {
            copyCommands.add("cp " + cacheDir + fontPackage.getNameFormatted() +
                    File.separator + font.getName() + " " + FONT_INSTALL_DIR);
        }

        copyCommands.add("cp " + FileUtils.getAssetsFile("DroidSansFallback.ttf", context)
                .getAbsolutePath() + " " + FONT_INSTALL_DIR);
        copyCommands = Collections.unmodifiableList(copyCommands);
    }

    public void install() {
        Observable.create(subscriber -> {
            if (Shell.SU.available()) {
                Shell.SU.run(MOUNT_SYSTEM);
                Shell.SU.run(copyCommands);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
