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

import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by priyeshpatel on 15-02-06.
 */
public class FontPackage {
    private String name;
    private String nameFormatted;
    private ArrayList<Font> fontList = new ArrayList<>();
    private static final List<String> styles = Arrays.asList(
            "Roboto-Bold.ttf",
            "Roboto-BoldItalic.ttf",
            "Roboto-Italic.ttf",
            "Roboto-Light.ttf",
            "Roboto-LightItalic.ttf",
            "Roboto-Regular.ttf",
            "Roboto-Thin.ttf",
            "Roboto-ThinItalic.ttf",
            "RobotoCondensed-Bold.ttf",
            "RobotoCondensed-BoldItalic.ttf",
            "RobotoCondensed-Italic.ttf",
            "RobotoCondensed-Regular.ttf"
    );

    private static final String BASE_URL = "https://raw.githubusercontent.com/ItsPriyesh/FontsterFontsRepo/master/";

    public FontPackage(String name) {
        this.name = name;
        this.nameFormatted = name.replace(" ", "") + "FontPack";

        generateFonts();
    }

    private void generateFonts() {
        for (String style : styles) {
            Font font = new Font(style, BASE_URL + nameFormatted + "/" + style);
            fontList.add(font);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) generateLollipopFonts();
    }

    private void generateLollipopFonts() {
        fontList.addAll(Arrays.asList(
                new Font("Roboto-Black.ttf", BASE_URL + nameFormatted + "/Roboto-Bold.ttf"),
                new Font("Roboto-BlackItalic.ttf", BASE_URL + nameFormatted + "/Roboto-BoldItalic.ttf"),
                new Font("Roboto-Medium.ttf", BASE_URL + nameFormatted + "/Roboto-Regular.ttf"),
                new Font("Roboto-MediumItalic.ttf", BASE_URL + nameFormatted + "/Roboto-Italic.ttf"),
                new Font("RobotoCondensed-Light.ttf", BASE_URL + nameFormatted + "/RobotoCondensed-Regular.ttf"),
                new Font("RobotoCondensed-LightItalic.ttf", BASE_URL + nameFormatted + "/RobotoCondensed-Italic.ttf")
        ));
    }

    public ArrayList<Font> getFontList() {
        return fontList;
    }

    public String getName() {
        return name;
    }

    public String getNameFormatted() {
        return nameFormatted;
    }

    public Font getFont(int style) {
        return fontList.get(style);
    }

}
