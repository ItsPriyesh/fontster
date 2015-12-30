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

import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import timber.log.Timber;

public final class FontDownloader {

  private static final OkHttpClient sClient = new OkHttpClient();

  private FontPackage mFontPackage;

  public FontDownloader(FontPackage fontPackage) {
    mFontPackage = fontPackage;
  }

  public Observable<File> downloadAllFonts() {
    Timber.i("downloadAllFonts: " + mFontPackage.getName());
    return downloadFonts(mFontPackage.getFontSet());
  }

  public Observable<File> downloadFontStyles(Style... styles) {
    Timber.i("downloadFontStyles: %s for %s", Arrays.toString(styles), mFontPackage.getName());
    return Observable.from(styles)
        .map(mFontPackage::getFont)
        .filter(font -> font != null)
        .collect(HashSet<Font>::new, HashSet::add)
        .flatMap(FontDownloader::downloadFonts);
  }

  public static Observable<File> downloadStyleFromPackages(List<FontPackage> packages, Style style) {
    Timber.i("downloadStyleFromPackages: " + style);
    return Observable.from(packages).flatMap(fontPackage ->
        downloadFonts(Collections.singleton(fontPackage.getFont(style))));
  }

  /* package */ static Observable<File> downloadFile(final String url, final String path) {
    return Observable.create(subscriber -> {
      final Request request = new Request.Builder().url(url).build();
      try {
        if (!subscriber.isUnsubscribed()) {
          final File file = new File(path);
          if (!file.exists()) {
            // noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            Timber.i("downloadFile: Downloading " + file.getName());
            final Response response = sClient.newCall(request).execute();
            final BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
          } else Timber.i("downloadFile: Retrieved from cache " + file.getName());

          subscriber.onNext(file);
          subscriber.onCompleted();
        }
      } catch (IOException e) {
        subscriber.onError(new DownloadException(e));
      }
    });
  }

  private static Observable<File> downloadFonts(Set<Font> fonts) {
    return Observable.from(fonts).flatMap(f ->
        downloadFile(f.getUrl(), f.getFile().getAbsolutePath()));
  }

  public static class DownloadException extends Exception {
    public DownloadException(Exception root) { super(root); }
  }
}
