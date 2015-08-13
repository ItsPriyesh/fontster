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

import android.os.AsyncTask;

import com.chromium.fontinstaller.BusProvider;
import com.chromium.fontinstaller.events.Event;

import timber.log.Timber;

public class CommandRunner extends AsyncTask<String, Void, Void> {

    private Event onCompleteEvent;


    public CommandRunner(Event onCompleteEvent) {
        this.onCompleteEvent = onCompleteEvent;
    }

    @Override
    protected void onPreExecute() {
        Timber.i("CommandTask started");
    }

    @Override
    protected Void doInBackground(String... commands) {
        for (String command : commands) Timber.i(command);



        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        Timber.i("CommandTask complete");
        BusProvider.getInstance().post(onCompleteEvent);
    }

}
