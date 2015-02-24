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
import java.util.HashMap;

/**
 * Created by priyeshpatel on 15-02-06.
 */
public class FontPackage {
    private String name;
    private String nameFormatted;
    private HashMap<Font, Style> fontList = new HashMap<>();

    private static final String BASE_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/";

    public FontPackage(String name) {
        this.name = name;
        this.nameFormatted = name.replace(" ", "") + "FontPack";

        generateFonts();
    }

    private void generateFonts() {
        for (Style style : Style.values()) {
            Font font = new Font(style, BASE_URL + nameFormatted + "/" + style.getRemoteName());
            fontList.put(font, style);
        }
    }

    public ArrayList<Font> getFontList() {
        return new ArrayList<>(fontList.keySet());
    }

    public String getName() {
        return name;
    }

    public String getNameFormatted() {
        return nameFormatted;
    }

    public Typeface getFont(Style style) {
        String path = FontInstaller.CACHE_DIR + nameFormatted + File.separator + style.getLocalName();
        return Typeface.createFromFile(path);
    }

}
