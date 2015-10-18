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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.BackupManager;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.util.AlertUtils;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.ViewUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chromium.fontinstaller.util.PreferencesManager.Keys;

public class BackupRestoreFragment extends Fragment {

    @Bind(R.id.backup_unavailable_container)
    ViewGroup noBackupContainer;

    @Bind(R.id.backup_available_container)
    ViewGroup backupContainer;

    @Bind(R.id.backup_name)
    TextView backupName;

    @Bind(R.id.backup_date)
    TextView backupDate;

    private BackupManager backupManager;
    private PreferencesManager preferences;

    public BackupRestoreFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_restore, container, false);
        ButterKnife.bind(this, view);

        ((BaseActivity) getActivity()).setToolbarTitle(getString(R.string.drawer_item_backup_restore));

        backupManager = new BackupManager();
        preferences = PreferencesManager.getInstance(getActivity());

        checkForBackup();

        return view;
    }

    private void checkForBackup() {
        if (backupManager.backupExists()) {
            if (noBackupContainer.getVisibility() == View.VISIBLE)
                slideUpAndRemove(noBackupContainer);

            setupBackupContainer();
        } else {
            slideUpAndRemove(backupContainer);
            slideUpAndAdd(noBackupContainer);
        }
    }

    private void slideUpAndRemove(View view) {
        ViewUtils.animSlideUp(view, getActivity());
        view.setVisibility(View.GONE);
    }

    private void slideUpAndAdd(View view) {
        ViewUtils.animSlideInBottom(view, getActivity());
        view.setVisibility(View.VISIBLE);
    }

    private void setupBackupContainer() {
        slideUpAndAdd(backupContainer);

        backupName.setText(preferences.getString(Keys.BACKUP_NAME));
        backupDate.setText(preferences.getString(Keys.BACKUP_DATE));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.backup_available_container)
    public void backupContainerClicked() {
        final String[] options = {
                getString(R.string.backup_restore_option_restore),
                getString(R.string.backup_restore_option_delete)
        };

        new AlertDialog.Builder(getActivity())
                .setItems(options, (dialog, index) -> {
                    switch (index) {
                        case 0:
                            backupManager.restore()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnCompleted(this::onRestoreComplete)
                                    .subscribe();
                            break;
                        case 1:
                            backupManager.deleteBackup()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnCompleted(this::onBackupDeleted)
                                    .subscribe();
                            break;
                    }
                }).create().show();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.backup_fab)
    public void backupFabClicked() {
        BackupDialogFragment backupDialog = new BackupDialogFragment();
        backupDialog.show(getActivity().getSupportFragmentManager(), "BackupDialogFragment");
        backupDialog.setOnBackupClickedListener(name ->
                        backupManager.backup()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnCompleted(() -> onBackupComplete(name))
                                .subscribe()
        );
    }

    public void onBackupComplete(String name) {
        preferences.setString(Keys.BACKUP_NAME, name);
        preferences.setString(Keys.BACKUP_DATE, BackupManager.DATE_FORMAT.format(new Date()));
        checkForBackup();
    }

    public void onBackupDeleted() {
        checkForBackup();
    }

    public void onRestoreComplete() {
        AlertUtils.showRebootAlert(getActivity());
    }

}
