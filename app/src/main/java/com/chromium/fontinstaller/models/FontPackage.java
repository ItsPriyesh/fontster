package com.chromium.fontinstaller.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by priyeshpatel on 15-02-06.
 */
public class FontPackage {
    private String name;
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
        this.name = name + "FontPack";

        generateFonts();
    }

    private void generateFonts() {
        for (String style : styles) {
            Font font = new Font(style, BASE_URL + name + "/" + style);
            this.fontList.add(font);
        }
    }

    public ArrayList<Font> getFontList() {
        return fontList;
    }

    public String getName() {
        return name;
    }

    public Font getFont(int style) {
        return fontList.get(style);
    }
}
