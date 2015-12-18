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


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chromium.fontinstaller.Injector;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.BackupManager;
import com.chromium.fontinstaller.ui.common.BaseActivity;
import com.chromium.fontinstaller.core.FontsterPreferences;
import com.chromium.fontinstaller.util.RebootDialog;
import com.chromium.fontinstaller.util.ViewUtils;

import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chromium.fontinstaller.core.FontsterPreferences.Key;

public class BackupRestoreFragment extends Fragment {

  @Bind(R.id.backup_unavailable_container)
  ViewGroup mNoBackupContainer;

  @Bind(R.id.backup_available_container)
  ViewGroup mBackupContainer;

  @Bind(R.id.backup_name)
  TextView mBackupNameView;

  @Bind(R.id.backup_date)
  TextView mBackupDateView;

  @Bind(R.id.backup_fab)
  FloatingActionButton mBackupFab;

  @Inject
  BackupManager mBackupManager;

  @Inject
  FontsterPreferences mPreferences;

  public BackupRestoreFragment() { }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Injector.get().inject(this);

    View view = inflater.inflate(R.layout.fragment_backup_restore, container, false);
    ButterKnife.bind(this, view);

    ((BaseActivity) getActivity()).setToolbarTitle(getString(R.string.drawer_item_backup_restore));

    mBackupContainer.setOnClickListener(v -> onBackupContainerClicked());
    mBackupFab.setOnClickListener(v -> onBackupFabClicked());

    checkForBackup();

    return view;
  }

  private void checkForBackup() {
    if (mBackupManager.backupExists()) {
      if (mNoBackupContainer.getVisibility() == View.VISIBLE) {
        hide(mNoBackupContainer);
      }
      setupBackupContainer();
    } else {
      hide(mBackupContainer);
      show(mNoBackupContainer);
    }
  }

  private void hide(View view) {
    ViewUtils.animSlideUp(view, getActivity());
    view.setVisibility(View.GONE);
  }

  private void show(View view) {
    ViewUtils.animSlideInBottom(view, getActivity());
    view.setVisibility(View.VISIBLE);
  }

  private void setupBackupContainer() {
    show(mBackupContainer);

    mBackupNameView.setText(mPreferences.getString(Key.BACKUP_NAME));
    mBackupDateView.setText(mPreferences.getString(Key.BACKUP_DATE));
  }

  private void onBackupContainerClicked() {
    final String[] options = {
        getString(R.string.backup_restore_option_restore),
        getString(R.string.backup_restore_option_delete)
    };

    new AlertDialog.Builder(getActivity())
        .setItems(options, (dialog, index) -> {
          switch (index) {
            case 0:
              mBackupManager.restore()
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(this::onRestoreComplete)
                  .subscribe();
              break;
            case 1:
              mBackupManager.deleteBackup()
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(this::onBackupDeleted)
                  .subscribe();
              break;
          }
        }).create().show();
  }

  private void onBackupFabClicked() {
    new CreateBackupDialog(getActivity(), backupName -> mBackupManager.backup()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> onBackupComplete(backupName))
        .subscribe()).show();
  }

  private void onBackupComplete(String name) {
    mPreferences.setString(Key.BACKUP_NAME, name);
    mPreferences.setString(Key.BACKUP_DATE, BackupManager.DATE_FORMAT.format(new Date()));
    checkForBackup();
  }

  private void onBackupDeleted() {
    checkForBackup();
  }

  private void onRestoreComplete() {
    final Activity activity = getActivity();
    if (activity != null && !activity.isFinishing()) {
      new RebootDialog(activity);
    }
  }

}
