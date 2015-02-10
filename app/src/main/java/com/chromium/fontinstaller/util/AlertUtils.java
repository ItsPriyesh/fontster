package com.chromium.fontinstaller.util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class AlertUtils {

    public static void showBasicAlert(String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alert.create().show();
    }

    public static void showBasicAlert(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alert.create().show();
    }
}
