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

public enum Style {
    BOLD,
    BOLD_ITALIC,
    ITALIC,
    LIGHT,
    LIGHT_ITALIC,
    REGULAR,
    THIN,
    THIN_ITALIC,
    CONDENSED_BOLD,
    CONDENSED_BOLD_ITALIC,
    CONDENSED_ITALIC,
    CONDENSED_REGULAR,

    // New fonts introduced in Android 5.0+
    BLACK,
    BLACK_ITALIC,
    MEDIUM,
    MEDIUM_ITALIC,
    CONDENSED_LIGHT,
    CONDENSED_LIGHT_ITALIC;

    @Override
    public String toString() {
        switch (this) {
            case BOLD:
                return "Roboto-Bold.ttf";
            case BOLD_ITALIC:
                return "Roboto-BoldItalic.ttf";
            case ITALIC:
                return "Roboto-Italic.ttf";
            case LIGHT:
                return "Roboto-Light.ttf";
            case LIGHT_ITALIC:
                return "Roboto-LightItalic.ttf";
            case REGULAR:
                return "Roboto-Regular.ttf";
            case THIN:
                return "Roboto-Thin.ttf";
            case THIN_ITALIC:
                return "Roboto-ThinItalic.ttf";
            case CONDENSED_BOLD:
                return "RobotoCondensed-Bold.ttf";
            case CONDENSED_BOLD_ITALIC:
                return "RobotoCondensed-BoldItalic.ttf";
            case CONDENSED_ITALIC:
                return "RobotoCondensed-Italic.ttf";
            case CONDENSED_REGULAR:
                return "RobotoCondensed-Regular.ttf";
            case BLACK:
                return "Roboto-Black.ttf";
            case BLACK_ITALIC:
                return "Roboto-BlackItalic.ttf";
            case MEDIUM:
                return "Roboto-Medium.ttf";
            case MEDIUM_ITALIC:
                return "Roboto-MediumItalic.ttf";
            case CONDENSED_LIGHT:
                return "RobotoCondensed-Light.ttf";
            case CONDENSED_LIGHT_ITALIC:
                return "RobotoCondensed-LightItalic.ttf";
            default:
                throw new IllegalArgumentException();
        }
    }

    // New styles introduced in Android 5.0 do not exist in the font repository
    // Instead we will reuse the older variants that are most similar
    public String getRemoteName() {
        switch (this) {
            case BLACK:
                return BOLD.toString();
            case BLACK_ITALIC:
                return BOLD_ITALIC.toString();
            case MEDIUM:
                return REGULAR.toString();
            case MEDIUM_ITALIC:
                return ITALIC.toString();
            case CONDENSED_LIGHT:
                return CONDENSED_REGULAR.toString();
            case CONDENSED_LIGHT_ITALIC:
                return CONDENSED_ITALIC.toString();
            default:
                return this.toString();
        }
    }

    public String getLocalName() {
        return this.toString();
    }

}