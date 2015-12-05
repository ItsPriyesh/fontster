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

import com.chromium.fontinstaller.util.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

public class FontDownloaderTest {

    private static final File TEST_FOLDER = new File("./TempTestFolder/");

    private static final String FONT_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/AleoFontPack/Roboto-Regular.ttf";
    private static final File FILE = new File(TEST_FOLDER, "Roboto-Regular.ttf");
    private static final long EXPECTED_FILE_SIZE = 62816;

    @Before public void setup() {
        //noinspection ResultOfMethodCallIgnored
        TEST_FOLDER.mkdirs();
    }

    @After public void tearDown() {
        FileUtils.deleteDirectory(TEST_FOLDER);
    }

    @Test public void testDownloadFile() throws Exception {
        final TestSubscriber<File> testSubscriber = new TestSubscriber<>();
        FontDownloader.downloadFile(FONT_URL, FILE.getPath()).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(FILE));

        final long downloadedFileSize = testSubscriber.getOnNextEvents().get(0).length();
        assertEquals(EXPECTED_FILE_SIZE, downloadedFileSize, 0);
    }

}