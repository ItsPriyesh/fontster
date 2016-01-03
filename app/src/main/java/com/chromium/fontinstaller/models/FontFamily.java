package com.chromium.fontinstaller.models;

import android.graphics.Typeface;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public enum FontFamily {
  SANS_SERIF(
      "sans-serif",
      Typeface.NORMAL,
      Typeface.ITALIC,
      Typeface.BOLD,
      Typeface.BOLD_ITALIC
  ),
  SANS_SERIF_LIGHT(
      "sans-serif-light",
      Typeface.NORMAL,
      Typeface.ITALIC
  ),
  SANS_SERIF_THIN(
      "sans-serif-thin",
      Typeface.NORMAL,
      Typeface.ITALIC
  ),
  SANS_SERIF_CONDENSED(
      "sans-serif-condensed",
      Typeface.NORMAL,
      Typeface.ITALIC,
      Typeface.BOLD,
      Typeface.BOLD_ITALIC
  ),
  SANS_SERIF_MEDIUM(
      "sans-serif-medium",
      Typeface.NORMAL,
      Typeface.ITALIC
  ),
  SANS_SERIF_BLACK(
      "sans-serif-black",
      Typeface.NORMAL,
      Typeface.ITALIC
  ),
  SANS_SERIF_CONDENSED_LIGHT(
      "sans-serif-condensed-light",
      Typeface.NORMAL,
      Typeface.ITALIC
  );

  private final String familyName;
  private final int[] styles;

  FontFamily(String familyName, int... styles) {
    this.familyName = familyName;
    this.styles = styles;
  }

  private static String styleToString(int style) {
    switch (style) {
      case Typeface.NORMAL: return "regular";
      case Typeface.ITALIC: return "italic";
      case Typeface.BOLD: return "bold";
      case Typeface.BOLD_ITALIC: return "bold italic";
      default: return null;
    }
  }

  public static List<Pair<String, Typeface>> getSystemTypefaces() {
    List<Pair<String, Typeface>> typefaces = new ArrayList<>();
    for (FontFamily family : values()) {
      for (int style : family.styles) {
        typefaces.add(Pair.create(
            family.familyName + " " + styleToString(style),
            Typeface.create(family.familyName, style)));
      }
    }
    return typefaces;
  }
}