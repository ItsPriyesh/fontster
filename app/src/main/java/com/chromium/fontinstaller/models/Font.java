package com.chromium.fontinstaller.models;

/**
 * Created by priyeshpatel on 15-02-07.
 */
public class Font {
    private String name;
    private int style;
    private String url;

    public static final int BOLD = 0;
    public static final int BOLD_ITALIC = 1;
    public static final int ITALIC = 2;
    public static final int LIGHT = 3;
    public static final int LIGHT_ITALIC = 4;
    public static final int REGULAR = 5;
    public static final int THIN = 6;
    public static final int THIN_ITALIC = 7;
    public static final int CONDENSED_BOLD = 8;
    public static final int CONDENSED_BOLD_ITALIC = 9;
    public static final int CONDENSED_ITALIC = 10;
    public static final int CONDENSED_REGULAR = 11;

    public Font(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
