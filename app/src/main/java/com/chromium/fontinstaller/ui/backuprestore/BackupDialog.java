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

package com.chromium.fontinstaller.ui.backuprestore;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chromium.fontinstaller.R;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.jakewharton.rxbinding.widget.RxTextView.textChanges;

public class BackupDialog extends AlertDialog {

    @Bind(R.id.input_layout)
    TextInputLayout mInputLayout;

    private Button mPositiveButton;
    private volatile boolean mNameIsValid;
    private final BackupDialogListener mListener;

    public interface BackupDialogListener {
        void onBackupButtonClicked(String backupName);
    }

    protected BackupDialog(Context context, BackupDialogListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.backup_dialog_title);

        final View view = View.inflate(getContext(), R.layout.backup_dialog, null);
        ButterKnife.bind(this, view);
        setView(view);

        final EditText inputView = (EditText) view.findViewById(R.id.input);
        final Subscription textChanges = textChanges(inputView)
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(s -> !TextUtils.isEmpty(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nameIsValid -> {
                    if (nameIsValid) enableOkButton();
                    else showError();
                });

        final String buttonTextPos = getContext().getString(R.string.backup_dialog_button_backup);
        setButton(BUTTON_POSITIVE, buttonTextPos, (dialog, which) -> {
            if (!mNameIsValid) return;
            textChanges.unsubscribe();
            mListener.onBackupButtonClicked(inputView.getText().toString());
        });

        final String buttonTextNeg = getContext().getString(R.string.cancel);
        setButton(BUTTON_NEGATIVE, buttonTextNeg, (dialog, which) -> {
            textChanges.unsubscribe();
            dismiss();
        });

        super.onCreate(savedInstanceState);

        mPositiveButton = getButton(AlertDialog.BUTTON_POSITIVE);
        mPositiveButton.setEnabled(false);
    }

    private void enableOkButton() {
        mNameIsValid = true;
        mPositiveButton.setEnabled(true);
        mInputLayout.setError(null);
    }

    private void showError() {
        mNameIsValid = false;
        mPositiveButton.setEnabled(false);
        mInputLayout.setError(getContext().getString(R.string.backup_dialog_invalid_name));
    }

}
