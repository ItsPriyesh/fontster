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

public final class SystemConstants {

  @SuppressLint("SdCardPath")
  static final String BACKUP_PATH = "/storage/emulated/0/Android/data/com.chromium.fontinstaller/Backup/";

  @SuppressLint("SdCardPath")
  public static final String CACHE_PATH = "/storage/emulated/0/Android/data/com.chromium.fontinstaller/cache/";

  static final String SYSTEM_FONT_PATH = "/system/fonts/";

  static final String MOUNT_SYSTEM_COMMAND = "mount -o rw,remount /system";

}
