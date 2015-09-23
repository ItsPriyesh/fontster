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

import android.content.Context;
import android.graphics.Typeface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FontPackage {

    private String name;
    private String nameFormatted;
    private HashMap<Font, Style> fontStyleHashMap = new HashMap<>();

    private static final String BASE_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/";

    public FontPackage(String name) {
        this.name = name;
        this.nameFormatted = name.replace(" ", "") + "FontPack";

        generateFonts();
    }

    private void generateFonts() {
        for (Style style : Style.values()) {
            Font font = new Font(style, BASE_URL + nameFormatted + "/" + style.getRemoteName());
            fontStyleHashMap.put(font, style);
        }
    }

    public ArrayList<Font> getFontList() {
        return new ArrayList<>(fontStyleHashMap.keySet());
    }

    public Map<Font, Style> getFontStyleMap() { return fontStyleHashMap; }

    public String getName() {
        return name;
    }

    public String getNameFormatted() {
        return nameFormatted;
    }

    public Typeface getTypeface(Style style, Context context) {
        String path = context.getExternalCacheDir() + File.separator +
                nameFormatted + File.separator + style.getLocalName();

        if (new File(path).exists()) {
            try { return Typeface.createFromFile(path); }
            catch (Exception e) { return Typeface.DEFAULT; }
        } else return Typeface.DEFAULT;
    }

    public Font getFont(Style style) {
        for (Font font : fontStyleHashMap.keySet())
            if (fontStyleHashMap.get(font).equals(style))
                return font;

        return null;
    }

}