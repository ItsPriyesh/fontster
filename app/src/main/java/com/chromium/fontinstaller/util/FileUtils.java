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

package com.chromium.fontinstaller.util;

import android.app.ActivityManager;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

  public static int getMaxCacheSize(Context context) {
    int memClass = ((ActivityManager)
        context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

    return 1024 * 1024 * memClass / 8;
  }

  public static File getAssetsFile(String fileName, Context context) {
    if (context == null) return null;
    File file = new File(context.getExternalCacheDir() + File.separator + fileName);
    if (!file.exists()) {
      try {
        InputStream in = context.getAssets().open(fileName);
        OutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
        in.close();
        out.close();
      } catch (IOException e) {
        file = null;
      }
    }
    return file;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void deleteDirectory(File directory) {
    if (directory.exists()) {
      final File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDirectory(file);
          } else {
            file.delete();
          }
        }
      }
    }
    directory.delete();
  }
}
