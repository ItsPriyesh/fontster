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

import com.chromium.fontinstaller.core.exceptions.ShellCommandException;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public final class CommandRunner {

  /**
   * Synchronously runs the specified shell commands as root.
   *
   * @return a list containing the output of the executed commands. Empty if there is no output.
   * @throws ShellCommandException in the event that command output is null. This occurs if an
   *                               error is encountered. May indicate that root is unavailable.
   */
  public static List<String> run(List<String> commands) throws ShellCommandException {
    final List<String> result = Shell.SU.run(commands);
    if (result == null) throw new ShellCommandException("Failed to run commands " + commands);
    return result;
  }

}
