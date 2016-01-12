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

package com.chromium.fontinstaller.models;

import android.graphics.Typeface;

import com.chromium.fontinstaller.core.SystemConstants;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FontPackage {

  protected final String mName;
  protected final Set<Font> mFontSet;

  /* package */ static final String BASE_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/";

  protected interface CacheProvider { String getCachePath(); }

  /**
   * Use this constructor to create font packages representing those
   * that exist in the FontsterFontsRepo. These packs are verified,
   * and can be used with FontDownloader.
   *
   * @param name The font package name from FontsterFontsRepo
   */
  public FontPackage(String name) {
    mName = name;
    mFontSet = buildFontSetForRemotePackage();
  }

  /**
   * Use this constructor to create font packages representing a user
   * specified local folder. These packs are unverified, and cannot be
   * used with FontDownloader. They can only be installed.
   *
   * @param folder The local folder containing the fonts
   */
  private FontPackage(File folder) {
    mName = folder.getName();
    mFontSet = buildFontSetForLocalPackage(folder);
  }

  public static FontPackage fromFolder(File folder) {
    if (!folder.isDirectory()) {
      throw new IllegalArgumentException("The specified file must be a directory");
    }

    return new FontPackage(folder);
  }

  protected CacheProvider cacheProvider() { return () -> SystemConstants.CACHE_PATH; }

  private Set<Font> buildFontSetForRemotePackage() {
    final Style[] styles = Style.values();
    final Set<Font> fonts = new HashSet<>(styles.length);
    for (Style style : styles) {
      final String url = BASE_URL + mName.replace(" ", "") + "FontPack/" + style.getRemoteName();
      final File file = new File(cacheProvider().getCachePath() + mName.replace(" ", "") + "FontPack/" + style.getLocalName());
      fonts.add(new Font(style, url, file));
    }
    return fonts;
  }

  private Set<Font> buildFontSetForLocalPackage(File folder) {
    final Set<Font> fonts = new HashSet<>();
    for (File file : folder.listFiles()) {
      for (Style style : Style.REMOTE_STYLES) {
        if (style.getRemoteName().equals(file.getName())) {
          fonts.add(new Font(style, null, file));
        }
      }
    }
    return fonts;
  }

  public String getName() {
    return mName;
  }

  public Set<Font> getFontSet() {
    return mFontSet;
  }

  public Typeface getTypeface(Style style) {
    final Font font = getFont(style);
    if (!font.getFile().exists()) return Typeface.DEFAULT;
    else {
      try {
        return Typeface.createFromFile(font.getFile());
      } catch (Exception e) {
        return Typeface.DEFAULT;
      }
    }
  }

  public Font getFont(Style style) {
    for (Font font : mFontSet) if (font.getStyle().equals(style)) return font;
    return null;
  }

  public static boolean validFontPackFolder(String path) {
    final File folder = new File(path);
    if (!folder.exists() || !folder.isDirectory()) return false;
    final String[] fileList = folder.list();
    if (fileList == null) return false;
    final Set<String> fileNameSet = new HashSet<>(Arrays.asList(fileList));
    return fileNameSet.containsAll(Style.REMOTE_STYLE_NAMES);
  }

}