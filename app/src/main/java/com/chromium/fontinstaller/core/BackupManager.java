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

import com.chromium.fontinstaller.events.BackupCompleteEvent;
import com.chromium.fontinstaller.events.BackupDeletedEvent;
import com.chromium.fontinstaller.events.RestoreCompleteTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by priyeshpatel on 15-03-12.
 */
public class BackupManager {

    private File backupDirectory;

    private final Date currentDate = Calendar.getInstance().getTime();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    private static final String SOURCE_DIR = "/system/fonts/";
    private static final String BACKUP_DIR = "/sdcard/Android/data/com.chromium.fontinstaller/cache/Backup/";

    public BackupManager() {
        createBackupDir();
    }

    private void createBackupDir() {
        backupDirectory = new File(BACKUP_DIR);
        backupDirectory.mkdirs();
    }

    public void backup(String name) {
        createBackupDir();
        BackupCompleteEvent event = new BackupCompleteEvent(name, dateFormat.format(currentDate));

        CommandRunner backupTask = new CommandRunner(event);
        backupTask.execute("cp -R " + SOURCE_DIR + ". " + BACKUP_DIR);
    }

    public void restore() {
        if (Shell.SU.available()) {
            List<String> restoreCommands = new ArrayList<>();

            for (File file : backupDirectory.listFiles()) {
                restoreCommands.add("cp " + file.getAbsolutePath() + " " + SOURCE_DIR);
            }

            CommandRunner restoreTask = new CommandRunner(new RestoreCompleteTask());
            restoreTask.execute(restoreCommands.toArray(new String[restoreCommands.size()]));
        }
    }

    public void deleteBackup() {
        CommandRunner deleteBackupTask = new CommandRunner(new BackupDeletedEvent());
        deleteBackupTask.execute("rm -rf " + BACKUP_DIR);
    }

    public boolean backupExists() {
        if (backupDirectory.listFiles() == null) return false;
        else if (backupDirectory.listFiles().length == 0) return false;
        else return true;
    }
}
