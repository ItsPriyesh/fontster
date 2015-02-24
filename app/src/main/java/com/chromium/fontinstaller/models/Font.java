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
    private Style style;
    private String url;

    public Font(Style style, String url) {
        this.style = style;
        this.url = url;
    }

    public String getName() {
        return style.getLocalName();
    }

    public String getUrl() {
        return url;
    }

}
