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

package com.chromium.fontinstaller.core;

import android.annotation.SuppressLint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.chromium.fontinstaller.core.SystemConstants.BACKUP_PATH;
import static com.chromium.fontinstaller.core.SystemConstants.MOUNT_SYSTEM_COMMAND;
import static com.chromium.fontinstaller.core.SystemConstants.SYSTEM_FONT_PATH;

public class BackupManager {

  private final File mBackupDirectory = new File(BACKUP_PATH);

  @SuppressLint("SimpleDateFormat")
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

  static final String BACKUP_COMMAND = String.format("cp -R %s. %s", SYSTEM_FONT_PATH, BACKUP_PATH);
  static final String RESTORE_COMMAND = "cp %s " + SYSTEM_FONT_PATH;
  static final String DELETE_BACKUP_COMMAND = "rm -rf " + BACKUP_PATH;
  static final String VALID_EXTENSION = ".TTF";

  public BackupManager() { }

  public Observable<String> backup() {
    if (!mBackupDirectory.isDirectory()) {
      //noinspection ResultOfMethodCallIgnored
      mBackupDirectory.mkdirs();
    } else {
      Timber.i("backup: A backup already exists, deleting it");
      deleteBackup().toBlocking().subscribe();
    }

    Timber.i("backup: Creating backup");
    return Observable.just(Arrays
        .asList(MOUNT_SYSTEM_COMMAND, BACKUP_COMMAND))
        .doOnNext(commands -> Timber.i("backup: Running commands: " + commands))
        .map(CommandRunner::run)
        .doOnNext(output -> Timber.i("backup: Shell output: " + output))
        .last()
        .map(output -> DATE_FORMAT.format(new Date()));
  }

  public Observable<List<String>> restore() {
    return Observable.from(mBackupDirectory.listFiles())
        .filter(file -> file.getName().toUpperCase().endsWith(VALID_EXTENSION))
        .map(file -> String.format(RESTORE_COMMAND, file.getAbsolutePath()))
        .toList()
        .startWith(Collections.singletonList(MOUNT_SYSTEM_COMMAND))
        .doOnNext(commands -> Timber.i("restore: Running commands: " + commands))
        .map(CommandRunner::run)
        .doOnNext(output -> Timber.i("restore: Shell output: " + output));
  }

  public Observable<List<String>> deleteBackup() {
    return Observable.just(CommandRunner
        .run(Collections.singletonList(DELETE_BACKUP_COMMAND)))
        .subscribeOn(Schedulers.io());
  }

  public boolean backupExists() {
    return mBackupDirectory.listFiles() != null
        && mBackupDirectory.listFiles().length != 0;
  }
}
