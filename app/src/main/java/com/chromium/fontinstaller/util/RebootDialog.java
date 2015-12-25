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

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.CommandRunner;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class RebootDialog extends AlertDialog {

  private static final String REGULAR_FONT = "Roboto-Regular.ttf";
  private static final String BOLD_FONT = "Roboto-Bold.ttf";

  public RebootDialog(Context context) {
    super(context);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTitle(R.string.reboot_dialog_title);

    final View view = View.inflate(getContext(), R.layout.reboot_dialog, null);
    ButterKnife.bind(this, view);
    setView(view);

    final AssetManager assets = view.getContext().getAssets();
    final Typeface regular = Typeface.createFromAsset(assets, REGULAR_FONT);
    final Typeface bold = Typeface.createFromAsset(assets, BOLD_FONT);

    final TextView rebootMessage = (TextView) view.findViewById(R.id.reboot_message);
    rebootMessage.setTypeface(regular);

    final String buttonText = view.getContext().getString(R.string.reboot_dialog_button_text);
    setButton(BUTTON_POSITIVE, buttonText, (dialog, which) -> {
      dialog.dismiss();
      CommandRunner.runCommands("reboot")
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();
    });

    super.onCreate(savedInstanceState);

    final Button button = getButton(AlertDialog.BUTTON_POSITIVE);
    button.setTypeface(bold);
  }

}
