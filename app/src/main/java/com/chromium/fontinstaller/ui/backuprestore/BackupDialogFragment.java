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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.chromium.fontinstaller.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BackupDialogFragment extends DialogFragment {

    @InjectView(R.id.input)
    EditText input;

    private BackupDialogListener listener;

    public BackupDialogFragment() {
        // Required empty public constructor
    }

    public interface BackupDialogListener {
        public void onBackupButtonClicked(String backupName);
    }

    public void setOnBackupClickedListener(BackupDialogListener listener){
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_dialog, container, false);
        ButterKnife.inject(this, view);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.backup_button)
    public void backupButtonClicked() {
        if (input.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter a name for the backup", Toast.LENGTH_SHORT)
                    .show();
        } else {
            listener.onBackupButtonClicked(input.getText().toString());
            dismiss();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancel_button)
    public void cancelButtonClicked() {
        dismiss();
    }

}
