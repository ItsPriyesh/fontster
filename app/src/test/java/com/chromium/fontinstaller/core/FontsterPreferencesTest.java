package com.chromium.fontinstaller.core;

import com.chromium.fontinstaller.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.chromium.fontinstaller.core.FontsterPreferences.Key;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FontsterPreferencesTest {

  private static final String TEST_BACKUP_NAME = "testplsbackup";

  private final FontsterPreferences mPreferences =
      new FontsterPreferences(RuntimeEnvironment.application);

  @Test public void testPutAndGetString() throws Exception {
    mPreferences.putString(Key.BACKUP_NAME, TEST_BACKUP_NAME);
    assertEquals(TEST_BACKUP_NAME, mPreferences.getString(Key.BACKUP_NAME));
  }

  @Test public void testPutAndGetBoolean() throws Exception {
    mPreferences.putBoolean(Key.ENABLE_TRUEFONT, true);
    assertEquals(true, mPreferences.getBoolean(Key.ENABLE_TRUEFONT));
  }
}