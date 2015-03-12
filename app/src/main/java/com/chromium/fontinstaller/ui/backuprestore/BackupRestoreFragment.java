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

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.BackupManager;
import com.chromium.fontinstaller.events.BackupCompleteEvent;
import com.chromium.fontinstaller.events.BackupDeletedEvent;
import com.chromium.fontinstaller.util.PreferencesManager;
import com.chromium.fontinstaller.util.ViewUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class BackupRestoreFragment extends Fragment {

    @InjectView(R.id.backup_unavailable_container)
    ViewGroup noBackupContainer;

    @InjectView(R.id.backup_available_container)
    ViewGroup backupContainer;

    @InjectView(R.id.backup_name)
    TextView backupName;

    @InjectView(R.id.backup_date)
    TextView backupDate;

    private BackupManager backupManager;
    private PreferencesManager prefs;

    public BackupRestoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_restore, container, false);
        ButterKnife.inject(this, view);

        backupManager = new BackupManager();
        prefs = PreferencesManager.getInstance(getActivity());

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

        backupName.setText(prefs.getString(PreferencesManager.KEY_BACKUP_NAME));
        backupDate.setText(prefs.getString(PreferencesManager.KEY_BACKUP_DATE));
    }

    @OnClick(R.id.backup_available_container)
    public void backupContainerClicked() {
        new AlertDialog.Builder(getActivity())
                .setItems(new String[] {"Restore", "Delete"}, (dialog, index) -> {
                    switch (index) {
                        case 0:
                            Timber.i("Restore");
                            break;
                        case 1:
                            backupManager.deleteBackup();
                            break;
                    }
                })
                .create().show();
    }

    @OnClick(R.id.backup_fab)
    public void backupFabClicked() {
        BackupDialogFragment backupDialog = new BackupDialogFragment();
        backupDialog.show(getActivity().getSupportFragmentManager(), "BackupDialogFragment");
        backupDialog.setOnBackupClickedListener(backupName -> backupManager.backup(backupName));
    }

    @Subscribe
    public void onBackupComplete(BackupCompleteEvent event) {
        prefs.setString(PreferencesManager.KEY_BACKUP_NAME, event.getBackupName());
        prefs.setString(PreferencesManager.KEY_BACKUP_DATE, event.getDate());

        checkForBackup();
    }

    @Subscribe
    public void onBackupDeleted(BackupDeletedEvent event) {
        checkForBackup();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

}
