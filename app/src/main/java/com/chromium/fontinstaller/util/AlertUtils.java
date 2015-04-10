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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.chromium.fontinstaller.R;

import butterknife.ButterKnife;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public class AlertUtils {

    public static void showRebootAlert(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reboot_dialog);

        Typeface robotoReg = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        Typeface robotoMed = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");

        TextView title = ButterKnife.findById(dialog, R.id.title);
        TextView content = ButterKnife.findById(dialog, R.id.content);
        Button reboot = ButterKnife.findById(dialog, R.id.reboot_button);

        title.setTypeface(robotoMed);
        content.setTypeface(robotoReg);
        reboot.setTypeface(robotoMed);

        reboot.setOnClickListener(v -> RootUtils.runBackgroundCommand("reboot", false, context));

        dialog.show();
    }
}
