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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.events.InstallCompleteEvent;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by priyeshpatel on 15-02-16.
 */
public class InstallTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private ProgressDialog installProgress;

    public static final String MOUNT_SYSTEM = "mount -o rw,remount /system";

    public InstallTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        installProgress = new ProgressDialog(context);
        installProgress.setMessage("Installing");
        installProgress.show();
    }

    @Override
    protected Void doInBackground(String... commands) {
        Shell.SU.run(MOUNT_SYSTEM);
        Shell.SU.run(commands);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        installProgress.dismiss();
        BusProvider.getInstance().post(new InstallCompleteEvent());
    }

}
