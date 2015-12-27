package com.chromium.fontinstaller;

import com.chromium.fontinstaller.models.FontPackage;

import java.io.File;

public final class MockFontPackage extends FontPackage {

  public static final File TEST_FOLDER = new File("./TempTestFolder/");

  public MockFontPackage(String name) { super(name); }

  @Override protected CacheProvider cacheProvider() {
    return () -> TEST_FOLDER.getPath() + File.separator;
  }
}