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

package com.chromium.fontinstaller.ui.settings.developer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.models.FontPackage;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.jakewharton.rxbinding.widget.RxTextView.textChanges;

public class FontPackPickerDialog extends AlertDialog {

  private TextInputLayout mInputLayout;
  private Button mPositiveButton;
  private final Action1<FontPackage> mCallback;
  private volatile boolean mPathIsValid = false;

  /* package */ FontPackPickerDialog(Context context, Action1<FontPackage> callback) {
    super(context);
    mCallback = callback;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setTitle(R.string.font_pack_picker_dialog_title);
    final View view = View.inflate(getContext(), R.layout.file_path_dialog, null);
    setView(view);

    mInputLayout = ButterKnife.findById(view, R.id.input_layout);
    final EditText inputView = ButterKnife.findById(view, R.id.file_path_input);
    final Subscription textChanges = textChanges(inputView)
        .debounce(400, TimeUnit.MILLISECONDS)
        .map(CharSequence::toString)
        .map(FontPackage::validFontPackFolder)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(pathIsValid -> {
          if (pathIsValid) enableOkButton();
          else showError();
        });

    setButton(BUTTON_POSITIVE, getContext().getString(R.string.ok), (dialog, which) -> {
      if (!mPathIsValid) return;
      textChanges.unsubscribe();
      mCallback.call(fontPackageFromEditText(inputView));
    });

    setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), (dialog, which) -> {
      textChanges.unsubscribe();
      dismiss();
    });

    super.onCreate(savedInstanceState);

    mPositiveButton = getButton(AlertDialog.BUTTON_POSITIVE);
    mPositiveButton.setEnabled(false);
  }

  private static FontPackage fontPackageFromEditText(EditText editText) {
    return FontPackage.fromFolder(new File(editText.getText().toString()));
  }

  private void enableOkButton() {
    mPathIsValid = true;
    mPositiveButton.setEnabled(true);
    mInputLayout.setError(null);
  }

  private void showError() {
    mPathIsValid = false;
    mPositiveButton.setEnabled(false);
    mInputLayout.setError(getContext().getString(R.string.font_pack_picker_dialog_invalid_path));
  }

}