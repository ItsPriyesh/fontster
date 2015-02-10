package com.chromium.fontinstaller.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.Toast;

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
    private Activity context;

    private enum CompletionStatus {INCOMPLETE, COMPLETE, ERROR}

    private HashMap<Font, CompletionStatus> hashMap = new HashMap<>(12);

    public FontDownloader(FontPackage fontPackage, Activity context) {
        this.fontPackage = fontPackage;
        this.context = context;

        createCacheDir();
    }

    public void download() {
        for (Font font : fontPackage.getFontList()) {
            hashMap.put(font, CompletionStatus.INCOMPLETE);
            File file = new File(context.getExternalCacheDir() + File.separator +
                    fontPackage.getName() + File.separator + font.getName());

            Ion.with(context).load(font.getUrl()).write(file)
                    .setCallback((e, downloadedFile) -> {
                        if (e != null) {
                            Timber.i("Download failed " + e);
                            hashMap.put(font, CompletionStatus.ERROR);
                            return;
                        }
                        hashMap.put(font, CompletionStatus.COMPLETE);
                        Timber.i("Download successful " + file);
                    });

            checkCompletion();
        }
    }

    private void createCacheDir() {
        File dir = new File(context.getExternalCacheDir() + "/" + fontPackage.getName());
        dir.mkdirs();
    }

    private void checkCompletion() {
        new Handler().postDelayed(() -> {
            if (hashMap.containsValue(CompletionStatus.INCOMPLETE)) checkCompletion();
            else evaluateCompletionStatus();
        }, 500);
    }

    private void evaluateCompletionStatus() {
        if (hashMap.containsValue(CompletionStatus.ERROR)) handleError();
        else Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show();
    }

    private void handleError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Download failed")
                .setMessage("An error was encountered while downloading the font pack.")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    deleteFontPack(new File(context.getExternalCacheDir() + File.separator, fontPackage.getName()), false);
                })
                .setPositiveButton("Retry", (dialog, which) -> {
                    deleteFontPack(new File(context.getExternalCacheDir() + File.separator, fontPackage.getName()), true);
                });
        builder.create().show();
    }

    private void deleteFontPack(final File fontPackDir, boolean retry) {
        final ProgressDialog dialog = ProgressDialog.show(context, null, "Removing corrupt files.", true);

        new Thread(() -> {
            deleteRecursive(fontPackDir);
            context.runOnUiThread(() -> {
                dialog.dismiss();
                if (retry) download();
            });
        }).start();
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory())
            for (File child : file.listFiles())
                deleteRecursive(child);

        file.delete();
    }
}
