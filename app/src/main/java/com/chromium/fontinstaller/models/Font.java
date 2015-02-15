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
