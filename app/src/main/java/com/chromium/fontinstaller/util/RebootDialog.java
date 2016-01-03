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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.CommandRunner;

import java.util.Collections;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chromium.fontinstaller.util.ViewUtils.toast;

public final class RebootDialog extends AlertDialog {

  /* Immediately after a font has been installed, the system won't be able
   * to provide fonts properly until after a reboot. As a workaround, these
   * assets are loaded and explicitly set to the necessary View's.
   */
  private static final String REGULAR_FONT = "Roboto-Regular.ttf";
  private static final String BOLD_FONT = "Roboto-Bold.ttf";

  public RebootDialog(Context context) {
    super(context);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);

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
      Observable.just("reboot")
          .map(Collections::singletonList)
          .map(CommandRunner::run)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(s -> { }, error -> toast(R.string.reboot_failed, getContext()));
    });

    super.onCreate(savedInstanceState);

    final Button button = getButton(AlertDialog.BUTTON_POSITIVE);
    button.setTypeface(bold);
  }

}
