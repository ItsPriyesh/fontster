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

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import rx.Observable;

public class BackupManager {

    private File backupDirectory;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    private static final String SOURCE_DIR = "/system/fonts/";
    private String backupDir;

    public BackupManager(Context context) {
        backupDir = context.getExternalCacheDir() + File.separator + "Backup" + File.separator;
        createBackupDir();
    }

    private void createBackupDir() {
        backupDirectory = new File(backupDir);
        backupDirectory.mkdirs();
    }

    public Observable<Void> backup() {
        createBackupDir();
        return CommandRunner.runCommand("cp -R " + SOURCE_DIR + ". " + backupDir);
    }

    public Observable<Void> restore() {
        if (Shell.SU.available()) {
            List<String> restoreCommands = new ArrayList<>();
            for (File file : backupDirectory.listFiles()) {
                restoreCommands.add("cp " + file.getAbsolutePath() + " " + SOURCE_DIR);
            }
            return CommandRunner.runCommands(restoreCommands);
        } else {
            return Observable.empty();
        }
    }

    public Observable<Void> deleteBackup() {
        return CommandRunner.runCommand("rm -rf " + backupDir);
    }

    public boolean backupExists() {
        return backupDirectory.listFiles() != null && backupDirectory.listFiles().length != 0;
    }
}
