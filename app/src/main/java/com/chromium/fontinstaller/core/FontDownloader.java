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
import android.os.Handler;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.events.DownloadCompleteEvent;
import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by priyeshpatel on 15-02-07.
 */
public class FontDownloader {

    private FontPackage fontPackage;
    private Context context;
    private enum CompletionStatus {INCOMPLETE, COMPLETE, ERROR}
    private HashMap<Font, CompletionStatus> hashMap = new HashMap<>(12);

    public FontDownloader(FontPackage fontPackage, Context context) {
        this.fontPackage = fontPackage;
        this.context = context;

        createCacheDir();
    }

    public void download() {
        for (Font font : fontPackage.getFontList()) {
            hashMap.put(font, CompletionStatus.INCOMPLETE);
            File file = new File(context.getExternalCacheDir() + File.separator +
                    fontPackage.getNameFormatted() + File.separator + font.getName());

            Ion.with(context).load(font.getUrl()).write(file)
                    .setCallback((e, downloadedFile) -> {
                        if (e != null) {
                            Timber.i("Download failed " + e);
                            hashMap.put(font, CompletionStatus.ERROR);
                        } else {
                            hashMap.put(font, CompletionStatus.COMPLETE);
                            Timber.i("Download successful " + file);
                        }
                    });
        }
        checkCompletion();
    }

    private void createCacheDir() {
        File dir = new File(context.getExternalCacheDir() + File.separator + fontPackage.getNameFormatted());
        dir.mkdirs();
    }

    private void checkCompletion() {
        new Handler().postDelayed(() -> {
            if (hashMap.containsValue(CompletionStatus.INCOMPLETE)) checkCompletion();
            else evaluateCompletionStatus();
        }, 500);
    }

    private void evaluateCompletionStatus() {
        if (hashMap.containsValue(CompletionStatus.ERROR)) {
            //handleError();
            Timber.i("Download failed");
            BusProvider.getInstance().post(new DownloadCompleteEvent(false));
        } else {
            Timber.i("Download success");
            BusProvider.getInstance().post(new DownloadCompleteEvent(true));
        }
    }
}