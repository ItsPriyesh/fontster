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

import java.util.Arrays;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import rx.Observable;

public class CommandRunner {

    public static Observable<Void> runCommands(List<String> commands) {
        return Observable
                .create(subscriber -> {
                    if (Shell.SU.available()) {
                        Shell.SU.run(commands);
                    }
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                });
    }

    public static Observable<Void> runCommand(String command) {
        return runCommands(Arrays.asList(command));
    }

}
