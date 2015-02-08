package com.chromium.fontinstaller.util;

import android.content.Context;

import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import timber.log.Timber;

/**
 * Created by priyeshpatel on 15-02-07.
 */
public class FontDownloader {
    FontPackage fontPackage;
    Context context;

    public FontDownloader(FontPackage fontPackage, Context context) {
        this.fontPackage = fontPackage;
        this.context = context;
    }

    public void download() {
        for (Font font : fontPackage.getFontList()) {
            Ion.with(context)
                    .load(font.getUrl())
                    .write(new File(context.getExternalCacheDir() + "/" + font.getName()))
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            if (e != null) {
                                Timber.i("Error downloading " + result);
                                return;
                            }
                            Timber.i("Downloaded " + result);
                        }
                    });
        }
    }
}
