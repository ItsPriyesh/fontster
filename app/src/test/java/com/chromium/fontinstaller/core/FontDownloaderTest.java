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

import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.util.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FontDownloaderTest {

  private static final File TEST_FOLDER = new File("./TempTestFolder/");

  private static final String FONT_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/AleoFontPack/Roboto-Regular.ttf";
  private static final File FILE = new File(TEST_FOLDER, "Roboto-Regular.ttf");
  private static final long EXPECTED_FILE_SIZE = 62816;

  private static final String MOCK_FONT_PACK_NAME = "Aleo";

  @Before public void createDownloadFolder() {
    // noinspection ResultOfMethodCallIgnored
    TEST_FOLDER.mkdirs();
  }

  @After public void deleteDownloadFolder() {
    FileUtils.deleteDirectory(TEST_FOLDER);
  }

  @Test public void testDownloadFile() throws Exception {
    TestSubscriber<File> testSubscriber = new TestSubscriber<>();
    FontDownloader.downloadFile(FONT_URL, FILE.getPath()).subscribe(testSubscriber);
    testSubscriber.assertNoErrors();
    testSubscriber.assertReceivedOnNext(Collections.singletonList(FILE));

    long downloadedFileSize = testSubscriber.getOnNextEvents().get(0).length();
    assertEquals(EXPECTED_FILE_SIZE, downloadedFileSize, 0);
  }

  @Test public void testDownloadAllFonts_downloadsAllStyles() throws Exception {
    TestSubscriber<File> testSubscriber = new TestSubscriber<>();

    FontDownloader fontDownloader = new FontDownloader(new MockFontPackage());
    fontDownloader.downloadAllFonts().subscribe(testSubscriber);
    testSubscriber.assertNoErrors();

    List<File> downloadedFiles = testSubscriber.getOnNextEvents();
    List<String> downloadedFileNames = new ArrayList<String>() {{
      for (File f : downloadedFiles) add(f.getName());
    }};

    List<Style> expectedStyles = new ArrayList<>(Arrays.asList(Style.values()));
    List<String> expectedStyleNames = new ArrayList<String>() {{
      for (Style s : expectedStyles) add(s.getLocalName());
    }};

    assertTrue(downloadedFileNames.containsAll(expectedStyleNames));
  }

  @Test public void testDownloadAllFonts_filesExist() throws Exception {
    TestSubscriber<File> testSubscriber = new TestSubscriber<>();

    FontDownloader fontDownloader = new FontDownloader(new MockFontPackage());
    fontDownloader.downloadAllFonts().subscribe(testSubscriber);
    testSubscriber.assertNoErrors();

    File downloadedFontPack = new File(TEST_FOLDER, MOCK_FONT_PACK_NAME + "FontPack");

    assertTrue(downloadedFontPack.exists());
    assertTrue(downloadedFontPack.isDirectory());
    assertEquals(Style.values().length, downloadedFontPack.listFiles().length);
  }

  private static final class MockFontPackage extends FontPackage {
    MockFontPackage() { super(MOCK_FONT_PACK_NAME); }

    @Override protected CacheProvider cacheProvider() {
      return () -> TEST_FOLDER.getPath() + File.separator;
    }
  }
}