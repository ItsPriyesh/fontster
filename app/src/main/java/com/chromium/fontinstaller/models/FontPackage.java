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

import com.chromium.fontinstaller.core.FontInstaller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FontPackage {

    private final String name;
    private boolean downloadable;
    private HashMap<Font, Style> fontsToStyles = new HashMap<>();

    private static final String BASE_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/";

    /**
     * Use this constructor to create font packages representing those
     * that exist in the FontsterFontsRepo. These packs are verified,
     * and can be used with FontDownloader.
     *
     * @param name The font package name from FontsterFontsRepo
     */
    public FontPackage(String name) {
        this.name = name;
        downloadable = true;
        initForDownloadableFontPack();
    }

    /**
     * Use this constructor to create font packages representing a user
     * specified local folder. These packs are unverified, and cannot be
     * used with FontDownloader. They can only be installed.
     *
     * @param folder The local folder containing the fonts
     */
    private FontPackage(File folder) {
        this.name = folder.getName();
        downloadable = false;
        initForLocalFontPack(folder);
    }

    public static FontPackage fromFolder(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The specified file must be a directory");
        }

        return new FontPackage(folder);
    }

    private void initForDownloadableFontPack() {
        for (Style style : Style.values()) {
            final String url = BASE_URL + name.replace(" ", "") + "FontPack/" + style.getRemoteName();
            final File file = new File(FontInstaller.CACHE_DIR + name.replace(" ", "") + "FontPack/" + style.getLocalName());
            final Font font = new Font(style, url, file);
            fontsToStyles.put(font, style);
        }
    }

    private void initForLocalFontPack(File folder) {
        final File[] files = folder.listFiles();
        final Map<String, File> namesToFiles = new HashMap<>();
        for (File file : files) {
            if (Style.REMOTE_STYLE_NAMES.contains(file.getName())) {
                namesToFiles.put(file.getName(), file);
            }
        }

        for (Style style : Style.REMOTE_STYLES) {
            final Font font = new Font(style, null, namesToFiles.get(style.getLocalName()));
            fontsToStyles.put(font, style);
        }
    }

    public ArrayList<Font> getFontList() {
        return new ArrayList<>(fontsToStyles.keySet());
    }

    public Map<Font, Style> getFontStyleMap() {
        return fontsToStyles;
    }

    public String getName() {
        return name;
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
        for (Font font : fontsToStyles.keySet())
            if (fontsToStyles.get(font).equals(style))
                return font;

        return null;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public static boolean validFontPackFolder(String path) {
        final File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) return false;
        final Set<String> fileNameSet = new HashSet<>(Arrays.asList(folder.list()));
        return fileNameSet.containsAll(Style.REMOTE_STYLE_NAMES);
    }

}