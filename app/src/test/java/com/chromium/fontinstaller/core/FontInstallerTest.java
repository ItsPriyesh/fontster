package com.chromium.fontinstaller.core;

import com.chromium.fontinstaller.MockFontPackage;
import com.chromium.fontinstaller.models.FontPackage;
import com.chromium.fontinstaller.models.Style;
import com.chromium.fontinstaller.util.FileUtils;

import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FontInstallerTest {

  private static final String FONT_NAME = "Aleo";
  private static final FontPackage MOCK_FONT_PACK = new MockFontPackage(FONT_NAME);

  private static final Set<String> EXPECTED_COMMANDS = new HashSet<String>() {{
    for (Style style : Style.values()) {
      add("cp " + MockFontPackage.TEST_FOLDER.getAbsolutePath() + File.separator + FONT_NAME
          + "FontPack" + File.separator + style.getLocalName() + " /system/fonts/");
    }
  }};

  private static void downloadFontPack() {
    // noinspection ResultOfMethodCallIgnored
    MockFontPackage.TEST_FOLDER.mkdirs();
    new FontDownloader(MOCK_FONT_PACK).downloadAllFonts().subscribe();
  }

  private static void deleteDownloadFolder() {
    FileUtils.deleteDirectory(MockFontPackage.TEST_FOLDER);
  }

  @Test public void testGenerateCommands_systemMountedFirst() throws Exception {
    downloadFontPack();
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    FontInstaller.generateCommands(MOCK_FONT_PACK, null).subscribe(testSubscriber);
    List<String> commands = testSubscriber.getOnNextEvents();
    assertEquals(SystemConstants.MOUNT_SYSTEM_COMMAND, commands.get(0));
    deleteDownloadFolder();
  }

  @Test public void testGenerateCommands_containsInstallCommands() throws Exception {
    downloadFontPack();
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    FontInstaller.generateCommands(MOCK_FONT_PACK, null).subscribe(testSubscriber);
    List<String> commands = testSubscriber.getOnNextEvents();
    assertTrue(commands.containsAll(EXPECTED_COMMANDS));
    deleteDownloadFolder();
  }

  @Test public void testGenerateCommands_installExceptionIfFilesNonexistent() throws Exception {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    FontInstaller.generateCommands(MOCK_FONT_PACK, null).subscribe(testSubscriber);
    testSubscriber.assertError(FontInstaller.InstallException.class);
  }
}