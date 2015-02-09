package com.chromium.fontinstaller.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;
import com.koushikdutta.ion.Ion;

import java.io.File;

import timber.log.Timber;

/**
 * Created by priyeshpatel on 15-02-07.
 */
public class FontDownloader {
    private FontPackage fontPackage;
    private Activity context;

    public FontDownloader(FontPackage fontPackage, Activity context) {
        this.fontPackage = fontPackage;
        this.context = context;

        createCacheDir();
    }

    public void createCacheDir() {
        File dir = new File(context.getExternalCacheDir() + "/" + fontPackage.getName());
        dir.mkdirs();
    }

    public void download() {
        for (Font font : fontPackage.getFontList()) {
            Ion.with(context)
                    .load(font.getUrl())
                    .write(new File(context.getExternalCacheDir() + "/" + fontPackage.getName() + "/" + font.getName()))
                    .setCallback((e, result) -> {
                        if (e != null) {
                            Timber.i("Download failed " + e);
                            return;
                        }
                        Timber.i("Download successful " + result);
                    });
        }
    }

    private void handleError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Download failed")
                .setMessage("An error was encountered while downloading the font pack.")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    deleteFontPack(new File(context.getExternalCacheDir() + "/", fontPackage.getName()), false);
                })
                .setPositiveButton("Retry", (dialog, which) -> {
                    deleteFontPack(new File(context.getExternalCacheDir() + "/", fontPackage.getName()), true);
                });
        builder.create().show();
    }

    private void deleteFontPack(final File fontPackDir, boolean retry) {
        final ProgressDialog dialog = ProgressDialog.show(context, null, "Removing corrupt files.", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteRecursive(fontPackDir);

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (retry) download();
                    }
                });
            }
        }).start();
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory())
            for (File child : file.listFiles())
                deleteRecursive(child);

        file.delete();
    }
}
