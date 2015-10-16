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

import android.util.Pair;

import com.chromium.fontinstaller.models.Font;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import timber.log.Timber;

public class FontDownloader {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static class DownloadException extends Exception {
        public DownloadException(Exception root) { super(root); }
    }

    public static Observable<File> downloadAllFonts(FontPackage fontPackage) {
        createCacheDirectory(fontPackage);
        return downloadFonts(fontPackage, allFontFinder);
    }

    public static Observable<File> downloadStyledFonts(FontPackage fontPackage, Style... styles) {
        createCacheDirectory(fontPackage);
        return downloadFonts(fontPackage, styledFontFinder(Arrays.asList(styles)));
    }

    private static Observable<File> downloadFile(final String url, final String path) {
        return Observable.create(subscriber -> {
            final Request request = new Request.Builder().url(url).build();
            try {
                if (!subscriber.isUnsubscribed()) {
                    final File file = new File(path);
                    if (!file.exists()) {
                        Timber.i("Downloading: " + file.getName());
                        final Response response = CLIENT.newCall(request).execute();
                        final BufferedSink sink = Okio.buffer(Okio.sink(file));
                        sink.writeAll(response.body().source());
                        sink.close();
                    } else Timber.i("From cache: " + file.getName());

                    subscriber.onNext(file);
                    subscriber.onCompleted();
                }
            } catch (IOException e) {
                subscriber.onError(new DownloadException(e));
            }
        });
    }

    private static Observable<File> downloadFiles(ArrayList<Pair<String, String>> files) {
        return Observable.from(files).flatMap(p -> downloadFile(p.first, p.second));
    }

    private static Observable<File> downloadFonts(FontPackage fontPackage, FontFinder finder) {
        ArrayList<Pair<String, String>> urlsAndPaths = new ArrayList<>();
        for (Font font : finder.findFonts(fontPackage)) {
            urlsAndPaths.add(new Pair<>(font.getUrl(), font.getFile().getAbsolutePath()));
        }
        return downloadFiles(urlsAndPaths);
    }

    private interface FontFinder {
        List<Font> findFonts(FontPackage fontPackage);
    }

    private static FontFinder allFontFinder = FontPackage::getFontList;

    private static FontFinder styledFontFinder(List<Style> acceptedStyles) {
        return fontPackage -> {
            Map<Font, Style> fontStyleMap = fontPackage.getFontStyleMap();
            ArrayList<Font> rightFonts = new ArrayList<>();
            for (Map.Entry<Font, Style> f : fontStyleMap.entrySet()) {
                if (acceptedStyles.contains(f.getValue())) {
                    rightFonts.add(f.getKey());
                }
            }
            return rightFonts;
        };
    }

    private static void createCacheDirectory(FontPackage fontPackage) {
        new File(fontPackage.getFontList().get(0).getFile().getAbsolutePath()).mkdirs();
    }

}
