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
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import rx.Observable;

import static com.chromium.fontinstaller.core.SystemConstants.*;

public class BackupManager {

    private File mBackupDirectory;

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    public BackupManager() {
        createBackupDir();
    }

    private void createBackupDir() {
        mBackupDirectory = new File(BACKUP_PATH);
        //noinspection ResultOfMethodCallIgnored
        mBackupDirectory.mkdirs();
    }

    public Observable<Void> backup() {
        createBackupDir();
        return CommandRunner.runCommand("cp -R " + SYSTEM_FONT_PATH + ". " + BACKUP_PATH);
    }

    public Observable<Void> restore() {
        if (Shell.SU.available()) {
            Shell.SU.run(MOUNT_SYSTEM_COMMAND);
            List<String> restoreCommands = new ArrayList<>();
            for (File file : mBackupDirectory.listFiles()) {
                restoreCommands.add("cp " + file.getAbsolutePath() + " " + SYSTEM_FONT_PATH);
            }
            return CommandRunner.runCommands(restoreCommands);
        } else {
            return Observable.empty();
        }
    }

    public Observable<Void> deleteBackup() {
        return CommandRunner.runCommand("rm -rf " + BACKUP_PATH);
    }

    public boolean backupExists() {
        return mBackupDirectory.listFiles() != null && mBackupDirectory.listFiles().length != 0;
    }
}
