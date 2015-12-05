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

package com.chromium.fontinstaller.ui.install;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public final class TryFontDialog extends AlertDialog {

  @Bind(R.id.input)
  EditText mInputView;

  private final FontPackage mFontPackage;
  private final Style mStyle;
  private final Action1<String> mCallback;

  protected TryFontDialog(FontPackage fontPackage, Style style, Action1<String> callback, Context context) {
    super(context);
    mFontPackage = fontPackage;
    mStyle = style;
    mCallback = callback;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTitle(mFontPackage.getName());

    final View view = View.inflate(getContext(), R.layout.try_font_dialog, null);
    ButterKnife.bind(this, view);
    setView(view);

    mInputView.setTypeface(mFontPackage.getTypeface(mStyle));

    final String buttonText = view.getContext().getString(R.string.done);
    setButton(BUTTON_POSITIVE, buttonText, (dialog, which) -> {
      dialog.dismiss();
      mCallback.call(mInputView.getText().toString());
    });

    super.onCreate(savedInstanceState);
  }

}
